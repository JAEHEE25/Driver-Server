package io.driver.codrive.modules.record.service.github;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubCommitService {
	private final WebClient webClient;
}
