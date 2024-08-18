package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CreatedRoomListResponse(
	@Schema(description = "참여 중인 그룹 목록 총 페이지 개수", examples = "2")
	int totalPage,

	@Schema(description = "생성한 그룹 목록", examples = {"""
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
	List<RoomItemResponse> createdRooms
) {
	public static CreatedRoomListResponse of(int totalPage, List<Room> createdRooms) {
		return CreatedRoomListResponse.builder()
			.totalPage(totalPage)
			.createdRooms(RoomItemResponse.of(createdRooms))
			.build();
	}
}