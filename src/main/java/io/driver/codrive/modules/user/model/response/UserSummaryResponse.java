package io.driver.codrive.modules.user.model.response;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserSummaryResponse(
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "닉네임", example = "닉네임")
	String nickname,

	@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
	String profileImg,

	@Schema(description = "주 언어", example = "Java")
	String language,

	@Schema(description = "성과율", example = "15")
	int successRate,

	@Schema(description = "가장 최근 푼 문제 제목", example = "가장 최근 푼 문제 제목")
	String recentProblemTitle
) {
	public static UserSummaryResponse of(User user) {
		return UserSummaryResponse.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImg(user.getProfileImg())
			.language(user.getLanguage().getName())
			.successRate(user.getSuccessRate())
			.recentProblemTitle(user.getRecentProblemTitle())
			.build();
	}
}
