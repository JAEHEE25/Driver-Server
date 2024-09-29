package io.driver.codrive.modules.record.service.github;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.global.util.TemplateUtils;
import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.model.dto.GithubCommitContentDto;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.record.model.dto.GithubRepositoryNameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubCommitService {
	private static final String TAG_PREFIX = "#";
	private static final String TAG_DELIMITER = ", ";
	private static final String PATH = "%s/%s/%s. %s.md";
	private static final String LEVEL_PREFIX = "Level.";
	private static final String CODE_BOX_START = "```%s\n";
	private static final String CODE_BOX_END = "\n```\n";
	private static final String TEMPLATE_PATH = "templates/template.md";
	private static final String COMMIT_MESSAGE = "[%s] %s";
	private static final String GITHUB_COMMIT_URL = "https://api.github.com/repos/%s/%s/contents/%s";
	private static final String GITHUB_REPOSITORY_LIST_URL = "https://api.github.com/user/repos?type=owner";
	private static final String AUTH_HEADER = "Authorization";
	private static final String AUTH_HEADER_VALUE = "Bearer %s";
	private static final String ACCEPT_HEADER = "Accept";
	private static final String ACCEPT_HEADER_VALUE = "application/vnd.github+json";

	private final WebClient webClient;
	private final GithubTokenService githubTokenService;

	@Transactional
	public void commitToGithub(Record record, User user) throws IOException {
		String accessToken = githubTokenService.getGithubTokenByUserId(user.getUserId()).getAccessToken();
		String message = String.format(COMMIT_MESSAGE, record.getTitle(), DateUtils.formatCreatedAtByMD(record.getCreatedAt()));
		String content = TemplateUtils.encodeBase64(getContent(record));
		String path = getPath(record);

		try {
			webClient.put()
				.uri(String.format(GITHUB_COMMIT_URL, user.getUsername(), user.getGithubRepositoryName(), path))
				.bodyValue(GithubCommitContentDto.of(message, content))
				.header(AUTH_HEADER, String.format(AUTH_HEADER_VALUE, accessToken))
				.header(ACCEPT_HEADER, ACCEPT_HEADER_VALUE)
				.retrieve()
				.toEntity(Void.class)
				.block();
			log.info("GitHub Commit 성공");
		} catch (Exception e) {
			log.info("GitHub Commit 실패: {}", e.getMessage());
			throw new InternalServerErrorApplicationException("GitHub Commit을 실패했습니다.");
		}
	}

	public boolean isExistRepository(User user, String githubRepositoryName) {
		List<String> repositories = getRepositoryNames(user).collectList().block();
		if (repositories == null || repositories.isEmpty()) {
			return false;
		}
		return repositories.contains(githubRepositoryName);
	}

	private Flux<String> getRepositoryNames(User user) {
		String accessToken = githubTokenService.getGithubTokenByUserId(user.getUserId()).getAccessToken();
		try {
			return webClient.get()
				.uri(String.format(GITHUB_REPOSITORY_LIST_URL))
				.header(AUTH_HEADER, String.format(AUTH_HEADER_VALUE, accessToken))
				.header(ACCEPT_HEADER, ACCEPT_HEADER_VALUE)
                .retrieve()
                .bodyToFlux(GithubRepositoryNameDto.class)
				.map(GithubRepositoryNameDto::getName);
		} catch (Exception e) {
			log.info("GitHub Repository 리스트 요청 실패: {}", e.getMessage());
			throw new InternalServerErrorApplicationException("GitHub Repository 리스트 요청을 실패했습니다.");
		}
	}

	private String getPath(Record record) {
		String platformDirectoryName = record.getPlatform().getName();
		String levelDirectoryName = LEVEL_PREFIX + record.getLevel();
		String solvedCount = String.valueOf(record.getUser().getSolvedCount());
		String title = record.getTitle();
		return String.format(PATH, platformDirectoryName, levelDirectoryName, solvedCount, title);
	}

	@Transactional
	public String getContent(Record record) throws IOException {
		String template = TemplateUtils.readTemplate(TEMPLATE_PATH);
		Map<String, String> params = getContentMap(record);
		return TemplateUtils.formatTemplate(template, params);
	}

	@Transactional
	protected Map<String, String> getContentMap(Record record) {
		Map<String, String> params = new HashMap<>();
		params.put("title", record.getTitle());
		params.put("platform", record.getPlatform().getName());
		params.put("problemUrl", record.getProblemUrl());
		params.put("level", String.valueOf(record.getLevel()));
		params.put("tags", getTags(record.getCategories()));
		params.put("codeblocks", getCodeblocks(record));
		return params;
	}

	@Transactional
	protected String getTags(List<String> categories) {
		return categories.stream()
			.map(category -> TAG_PREFIX + category)
			.collect(Collectors.joining(TAG_DELIMITER));
	}

	@Transactional
	protected String getCodeblocks(Record record) {
		List<Codeblock> codeblocks = record.getCodeblocks();
		String startBox = String.format(CODE_BOX_START, record.getUser().getLanguage().getName());
		StringBuilder stringBuilder = new StringBuilder();

		codeblocks.forEach(codeblock -> {
			String code = startBox + codeblock.getCode() + CODE_BOX_END;
			String memo = TemplateUtils.replaceNewLineTag(codeblock.getMemo());
			stringBuilder.append(code);
			if (memo != null && !memo.isEmpty()) {
				stringBuilder.append(memo).append("\n");
			}
		});
		return stringBuilder.toString();
	}

}
