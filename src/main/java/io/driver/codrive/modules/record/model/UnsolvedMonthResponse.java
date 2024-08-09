package io.driver.codrive.modules.record.model;

import java.util.List;

import lombok.Builder;

@Builder
public record UnsolvedMonthResponse(
	List<Integer> months
) {
	public static UnsolvedMonthResponse of(List<Integer> months) {
		return UnsolvedMonthResponse.builder().months(months).build();
	}
}
