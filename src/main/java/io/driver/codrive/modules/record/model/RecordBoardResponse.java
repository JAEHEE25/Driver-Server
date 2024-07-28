package io.driver.codrive.modules.record.model;

import java.util.List;

import lombok.Builder;

@Builder
public record RecordBoardResponse(
	List<BoardResponse> boards

) {
	public static RecordBoardResponse of(List<BoardResponse> boards) {
		return RecordBoardResponse.builder()
			.boards(boards)
			.build();
	}
}
