package io.driver.codrive.modules.room.model;

import java.util.List;

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
		               "capacity": 20,
		               "tags": [
		                   "Java"
		               ],
		               "introduce": "그룹 한 줄 소개",
		               "information": "진행 방식
		           }
		       ]
	"""})
	List<RoomDetailResponse> recommendRooms
) {

	public static RoomRecommendResponse of(List<RoomDetailResponse> recommendRooms) {
		return RoomRecommendResponse.builder()
			.recommendRooms(recommendRooms)
			.build();
	}
}
