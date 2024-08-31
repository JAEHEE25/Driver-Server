package io.driver.codrive.modules.user.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserItemResponse(
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "닉네임", example = "닉네임")
	String nickname,

	@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
	String profileImg,

	@Schema(description = "주 언어", example = "Java")
	String language,

	@Schema(description = "GitHub URL", example = "GITHUB_URL")
	String githubUrl,

	@Schema(description = "팔로우 여부 (본인일 경우 null)", example = "true")
	Boolean isFollowing
) {
	public static List<UserItemResponse> of(List<User> users, User currentUser) {
		return users.stream()
			.map(user -> UserItemResponse.of(user, currentUser.isFollowing(user)))
			.toList();
	}

	public static List<UserItemResponse> of(List<User> users, boolean isFollowing) {
		return users.stream()
			.map(user -> UserItemResponse.of(user, isFollowing))
			.toList();
	}

	public static UserItemResponse of(User user, Boolean isFollowing) {
		return UserItemResponse.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImg(user.getProfileImg())
			.language(user.getLanguage().getName())
			.githubUrl(user.getGithubUrl())
			.isFollowing(isFollowing)
			.build();
	}

}
