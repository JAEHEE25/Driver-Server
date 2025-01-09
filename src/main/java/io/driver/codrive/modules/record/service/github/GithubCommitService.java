package io.driver.codrive.modules.record.service.github;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.global.util.TemplateUtils;
import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.model.dto.GithubCommitContentDto;
import io.driver.codrive.modules.record.model.dto.GithubContentDto;
import io.driver.codrive.modules.record.model.dto.GithubDeleteDto;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.record.model.dto.GithubRepositoryNameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	private static final String GITHUB_CONTENT_URL = "https://api.github.com/repos/%s/%s/contents/%s";
	private static final String GITHUB_REPOSITORY_LIST_URL = "https://api.github.com/user/repos?type=owner";
	private static final String AUTH_HEADER = "Authorization";
	private static final String AUTH_HEADER_VALUE = "Bearer %s";
	private static final String ACCEPT_HEADER = "Accept";
	private static final String ACCEPT_HEADER_VALUE = "application/vnd.github+json";

	private final WebClient webClient;
	private final GithubTokenService githubTokenService;

	@Transactional
	public void commitToGithub(Record record, User user, String path) throws IOException {
		String accessToken = githubTokenService.getGithubTokenByUserId(user.getUserId()).getAccessToken();
		String message = String.format(COMMIT_MESSAGE, record.getTitle(),
			DateUtils.formatCreatedAtByMD(record.getCreatedAt()));
		String content = TemplateUtils.encodeBase64(getContent(record));

		try {
			webClient.put()
				.uri(String.format(GITHUB_CONTENT_URL, user.getUsername(), user.getGithubRepositoryName(), path))
				.bodyValue(GithubCommitContentDto.of(message, content))
				.header(AUTH_HEADER, String.format(AUTH_HEADER_VALUE, accessToken))
				.header(ACCEPT_HEADER, ACCEPT_HEADER_VALUE)
				.retrieve()
				.toEntity(Void.class)
				.block();
			log.info("GitHub Commit 성공");
		} catch (Exception e) {
			log.info("GitHub Commit 실패: {}", e.getMessage());
			throw new InternalServerErrorApplicationException("깃허브 커밋을 실패했습니다. 리포지토리 이름을 등록해주세요.");
		}
	}

	public String getPath(Record record, Long recordNum) {
		String platformDirectoryName = record.getPlatform().getName();
		String levelDirectoryName = LEVEL_PREFIX + record.getLevel();
		String title = record.getTitle();
		return String.format(PATH, platformDirectoryName, levelDirectoryName, recordNum, title);
	}

	public String getContent(Record record) throws IOException {
		String template = TemplateUtils.readTemplate(TEMPLATE_PATH);
		Map<String, String> params = getContentMap(record);
		return TemplateUtils.formatTemplate(template, params);
	}

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

	protected String getTags(List<String> categories) {
		return categories.stream()
			.map(category -> TAG_PREFIX + category)
			.collect(Collectors.joining(TAG_DELIMITER));
	}

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

	public String getGithubContentSha(User user, String path) {
		GithubContentDto contentDto = getGithubContent(user, path);

		if (contentDto == null) {
			throw new InternalServerErrorApplicationException("GitHub Content 조회를 실패했습니다.");
		}

		if (contentDto.getPath().equals(path)) {
			return contentDto.getSha();
		} else {
			throw new InternalServerErrorApplicationException("GitHub Content Path가 일치하지 않습니다.");
		}
	}

	public GithubContentDto getGithubContent(User user, String path) {
		String accessToken = githubTokenService.getGithubTokenByUserId(user.getUserId()).getAccessToken();

		try {
			return webClient.get()
				.uri(String.format(GITHUB_CONTENT_URL, user.getUsername(), user.getGithubRepositoryName(), path))
				.header(AUTH_HEADER, String.format(AUTH_HEADER_VALUE, accessToken))
				.header(ACCEPT_HEADER, ACCEPT_HEADER_VALUE)
				.retrieve()
				.bodyToMono(GithubContentDto.class)
				.block();
		} catch (Exception e) {
			log.info("GitHub Content 조회 실패: {}", e.getMessage());
			throw new InternalServerErrorApplicationException("GitHub Content 조회를 실패했습니다.");
		}
	}

	public void deleteGithubContent(Record record, User user, String path, String sha) {
		String accessToken = githubTokenService.getGithubTokenByUserId(user.getUserId()).getAccessToken();
		String message = String.format(COMMIT_MESSAGE, record.getTitle(),
			DateUtils.formatCreatedAtByMD(record.getCreatedAt()));

		try {
			webClient.method(HttpMethod.DELETE)
				.uri(String.format(GITHUB_CONTENT_URL, user.getUsername(), user.getGithubRepositoryName(), path))
				.bodyValue(GithubDeleteDto.of(message, sha))
				.header(AUTH_HEADER, String.format(AUTH_HEADER_VALUE, accessToken))
				.header(ACCEPT_HEADER, ACCEPT_HEADER_VALUE)
				.retrieve()
				.toEntity(Void.class)
				.block();
			log.info("GitHub Content 삭제 성공");
		} catch (Exception e) {
			log.info("GitHub Content 삭제 실패: {}", e.getMessage());
			throw new InternalServerErrorApplicationException("GitHub 삭제를 실패했습니다.");
		}
	}

	public boolean isExistRepository(Long userId, String githubRepositoryName) {
		List<String> repositories = getRepositoryNames(userId);
		if (repositories == null || repositories.isEmpty()) {
			return false;
		}
		return repositories.contains(githubRepositoryName);
	}

	private List<String> getRepositoryNames(Long userId) {
		String accessToken = githubTokenService.getGithubAccessToken(userId);
		try {
			return webClient.get()
				.uri(String.format(GITHUB_REPOSITORY_LIST_URL))
				.header(AUTH_HEADER, String.format(AUTH_HEADER_VALUE, accessToken))
				.header(ACCEPT_HEADER, ACCEPT_HEADER_VALUE)
				.retrieve()
				.bodyToFlux(GithubRepositoryNameDto.class)
				.map(GithubRepositoryNameDto::getName)
				.collectList().block();
		} catch (Exception e) {
			log.info("GitHub Repository 리스트 요청 실패: {}", e.getMessage());
			throw new InternalServerErrorApplicationException("GitHub Repository 리스트 요청을 실패했습니다.");
		}
	}

}
