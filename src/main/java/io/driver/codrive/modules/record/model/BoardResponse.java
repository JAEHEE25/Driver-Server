package io.driver.codrive.modules.record.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
	public record BoardResponse(
		@Schema(description = "날짜", example = "5")
		String date,

		@Schema(description = "개수", example = "2")
		Long count
	) {
		public static BoardResponse of(String date, Long count) {
			return BoardResponse.builder()
				.date(date)
				.count(count)
				.build();
		}
	}