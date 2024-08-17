package io.driver.codrive.modules.room.model.response;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record OwnerDetailResponse(
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "사용자 닉네임", example = "닉네임")
	String nickname,

	@Schema(description = "사용자 프로필 이미지 URL", example = "IMAGE_URL")
	String profileImg
) {
	public static OwnerDetailResponse of(User owner) {
		return OwnerDetailResponse.builder()
			.userId(owner.getUserId())
			.nickname(owner.getNickname())
			.profileImg(owner.getProfileImg())
			.build();
	}
}
