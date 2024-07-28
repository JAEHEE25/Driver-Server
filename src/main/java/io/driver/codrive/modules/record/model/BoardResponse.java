package io.driver.codrive.modules.record.model;

import lombok.Builder;

@Builder
	public record BoardResponse(
		String date,
		Long count
	) {
		public static BoardResponse of(String date, Long count) {
			return BoardResponse.builder()
				.date(date)
				.count(count)
				.build();
		}
	}