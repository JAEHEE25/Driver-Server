package io.driver.codrive.modules.user.domain;

import io.driver.codrive.modules.global.exception.IllegalArgumentApplicationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
	NOT_SELECETED("선택 안 함"),
	PYTHON("Python"),
	JAVA("Java"),
	JAVASCRIPT("JavaScript"),
	CPP("C++"),
	C("C"),
	CSHARP("C#"),
	KOTLIN("Kotlin"),
	GO("Go"),
	RUBY("Ruby"),
	SWIFT("Swift"),
	SCALA("Scala");

	private final String displayName;

	public static Language of(String request) {
		for (Language language : values()) {
			if (language.displayName.equals(request)) {
				return language;
			}
		}
		throw new IllegalArgumentApplicationException("지원하지 않는 언어입니다.");
	}
}
