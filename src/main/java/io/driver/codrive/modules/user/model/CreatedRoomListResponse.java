package io.driver.codrive.modules.user.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.model.RoomDetailResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CreatedRoomListResponse(
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
		    	"capacity": 20,
		    	"tags": [
		    		"Java",
		        	"Python"
		   		],
		   		"introduce": "그룹 한 줄 소개",
		    	"information": "진행 방식"
		    }
		]
		"""
	})
	List<RoomDetailResponse> createdRooms
) {
	public static CreatedRoomListResponse of(List<Room> createdRooms) {
		return CreatedRoomListResponse.builder()
			.createdRooms(RoomDetailResponse.of(createdRooms))
			.build();
	}
}