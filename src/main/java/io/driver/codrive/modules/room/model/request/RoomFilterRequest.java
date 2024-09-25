package io.driver.codrive.modules.room.model.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record RoomFilterRequest(
	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]")
	List<String> tags,

	@Schema(description = "최소 인원", example = "0")
	Integer min,

	@Schema(description = "최대 인원", example = "50")
	Integer max
) {
}
