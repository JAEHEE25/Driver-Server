package io.driver.codrive.modules.record.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordBoardResponse(
	@Schema(description = "날짜별 문제 풀이 개수", implementation = BoardResponse.class)
	List<BoardResponse> boards

) {
	public static RecordBoardResponse of(List<BoardResponse> boards) {
		return RecordBoardResponse.builder()
			.boards(boards)
			.build();
	}
}
