package io.driver.codrive.modules.auth.model;

import lombok.Getter;

public record LoginRequest(
	String accessToken
) {}
