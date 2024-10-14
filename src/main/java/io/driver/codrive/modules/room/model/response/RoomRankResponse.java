package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomRankResponse(
	List<RoomRankUserResponse> rank
) {
	public static RoomRankResponse of(List<User> members) {
		return RoomRankResponse.builder()
			.rank(members.stream().map(RoomRankUserResponse::of).toList())
			.build();
	}

	@Builder
	public record RoomRankUserResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "닉네임", example = "닉네임")
		String nickname,

		@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
		String profileImg,

		@Schema(description = "주 언어", example = "Java")
		String language
	) {
		public static RoomRankUserResponse of(User user) {
			return RoomRankUserResponse.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profileImg(user.getProfileImg())
				.language(user.getLanguage().getName())
				.build();
		}
	}
}
