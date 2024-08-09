package io.driver.codrive.modules.record.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {
	BAEKJOON("백준"),
	PROGRAMMERS("프로그래머스"),
	SWEA("SWEA"),
	LEETCODE("리트코드"),
	HACKERRANK("해커랭크"),
	OTHER("기타");

	private final String name;

	public static Platform getPlatformByName(String requestPlatform) {
		return Arrays.stream(values()).filter(platform -> platform.name.equals(requestPlatform))
			.findFirst().orElseThrow(() -> new IllegalArgumentException("지원하지 않는 플랫폼입니다."));
	}
}
