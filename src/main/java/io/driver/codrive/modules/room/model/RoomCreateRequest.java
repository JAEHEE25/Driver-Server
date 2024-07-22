package io.driver.codrive.modules.room.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RoomCreateRequest(
	@Size(min = 1, max = 20, message = "그룹 제목은 {min}자 이상 {max}자 이하로 입력해주세요.")
	String title,

	@Size(max = 20, message = "비밀번호는 {max}자 이하로 입력해주세요.")
	String password,

	String imageUrl,

	@Range(min = 1, max = 50, message = "모집 인원은 {min}명 이상 {max}명 이하로 입력해주세요.")
	int capacity,

	@Size(min = 1, max = 5, message = "언어는 {min}개 이상 {max}개 이하로 선택해주세요.")
	List<String> languages,

	@Size(min = 1, max = 60, message = "한 줄 소개는 {min}자 이상 {max}자 이하로 입력해주세요.")
	String introduction,

	@Size(min = 1, max = 1000, message = "진행 방식은 {min}자 이상 {max}자 이하로 입력해주세요.")
	String information
) {
	public Room toEntity(User user) {
		return Room.builder()
			.title(title)
			.password(password)
			.imageUrl(imageUrl)
			.capacity(capacity)
			.introduction(introduction)
			.information(information)
			.user(user)
			.roomLanguageMappings(new ArrayList<>())
			.roomUserMappings(new ArrayList<>())
			.build();
	}
}