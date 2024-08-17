package io.driver.codrive.modules.room.model.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record JoinedRoomListResponse(
	@Schema(description = "참여 중인 그룹 목록 총 페이지 개수", examples = "2")
	int totalPage,

	@Schema(description = "참여 중인 그룹 목록", examples = {"""
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
	List<RoomItemResponse> joinedRooms
) {
	public static JoinedRoomListResponse of(int totalPage, List<Room> joinedRooms) {
		return JoinedRoomListResponse.builder()
			.totalPage(totalPage)
			.joinedRooms(RoomItemResponse.of(joinedRooms))
			.build();
	}
}