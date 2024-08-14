package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomRecommendResponse(
	@Schema(description = "오늘의 추천 그룹 목록", examples = {"""
		[
			{
			        "roomId": 1,
			        "title": "그룹 제목",
			        "owner": {
			          "userId": 1,
			          "nickname": "닉네임",
			          "profileImg": "IMAGE_URL"
			        },
			        "imageSrc": "IMAGE_URL",
			        "memberCount": 10,
			        "capacity": 20,
			        "tags": [
			          "Python"
			        ],
			        "introduce": "그룹 한 줄 소개"
			}
		]
		"""}, implementation = RoomItemResponse.class)
	List<RoomItemResponse> rooms
) {
	public static RoomRecommendResponse of(List<Room> rooms) {
		return RoomRecommendResponse.builder()
			.rooms(RoomItemResponse.of(rooms))
			.build();
	}
}
