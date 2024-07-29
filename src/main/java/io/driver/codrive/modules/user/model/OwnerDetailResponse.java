package io.driver.codrive.modules.user.model;

import io.driver.codrive.modules.user.domain.User;
import lombok.Builder;

@Builder
public record OwnerDetailResponse(
	Long userId,
	String nickname,
	String profileUrl
) {
	public static OwnerDetailResponse of(User owner) {
		return OwnerDetailResponse.builder()
			.userId(owner.getUserId())
			.nickname(owner.getNickname())
			.profileUrl(owner.getProfileUrl())
			.build();
	}
}
