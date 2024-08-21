package io.driver.codrive.modules.record.model.response;

import lombok.Builder;

@Builder
public record WeeklyRecordCountResponse(
	String nickname,
	int count
) {
	public static WeeklyRecordCountResponse of(String nickname, int count) {
		return WeeklyRecordCountResponse.builder()
			.nickname(nickname)
			.count(count)
			.build();
	}
}
