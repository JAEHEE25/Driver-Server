package io.driver.codrive.modules.user.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RandomUserListResponse(
	@Schema(description = "랜덤 사용자 목록")
	List<RandomUserItemResponse> randomUsers
) {
	public static RandomUserListResponse of(List<User> users) {
		return RandomUserListResponse.builder()
			.randomUsers(RandomUserItemResponse.of(users))
			.build();
	}

	@Builder
	record RandomUserItemResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "닉네임", example = "닉네임")
		String nickname,

		@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
		String profileImg,

		@Schema(description = "주 언어", example = "Java")
		String language,

		@Schema(description = "GitHub URL", example = "GITHUB_URL")
		String githubUrl
	) {
		public static List<RandomUserItemResponse> of(List<User> users) {
			return users.stream()
				.map(RandomUserItemResponse::of)
				.toList();
		}

		public static RandomUserItemResponse of(User user) {
			return RandomUserItemResponse.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profileImg(user.getProfileImg())
				.language(user.getLanguage().getName())
				.githubUrl(user.getGithubUrl())
				.build();
		}
	}
}
