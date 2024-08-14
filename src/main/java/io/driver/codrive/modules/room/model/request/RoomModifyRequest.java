package io.driver.codrive.modules.room.model.request;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomModifyRequest(
	@Schema(description = "그룹 제목", example = "그룹 제목", minLength = 1, maxLength = 20)
	@NotBlank(message = "그룹 제목을 입력해주세요.")
	@Size(min = 1, max = 20, message = "그룹 제목은 {min}자 이상 {max}자 이하로 입력해주세요.")
	String title,

	@Schema(description = "비밀번호", example = "비밀번호", maxLength = 20, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@Size(max = 20, message = "비밀번호는 {max}자 이하로 입력해주세요.")
	String password,

	@Schema(description = "모집 인원", example = "20", minimum = "1", maximum = "50")
	@Range(min = 1, max = 50, message = "모집 인원은 {min}명 이상 {max}명 이하로 입력해주세요.")
	int capacity,

	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]", minProperties = 1,
			allowableValues = {"Python", "Java", "JavaScript", "C++", "C", "C#", "Kotlin", "Go", "Ruby", "Swift", "Scala"})
	@NotNull(message = "언어 태그를 선택해주세요.")
	@Size(min = 1, message = "언어는 {min}개 이상 선택해주세요.")
	List<String> tags,

	@Schema(description = "그룹 한 줄 소개", example = "그룹 한 줄 소개", minLength = 1, maxLength = 60)
	@NotBlank(message = "그룹 한 줄 소개를 입력해주세요.")
	@Size(min = 1, max = 60, message = "그룹 소개는 {min}자 이상 {max}자 이하로 입력해주세요.")
	String introduce,

	@Schema(description = "진행 방식", example = "진행 방식", minLength = 1, maxLength = 1000)
	@NotBlank(message = "진행 방식을 입력해주세요.")
	@Size(min = 1, max = 1000, message = "진행 방식은 {min}자 이상 {max}자 이하로 입력해주세요.")
	String information
) {
	public Room toRoom(String imageSrc) {
		return Room.builder()
			.title(title)
			.password(password)
			.imageSrc(imageSrc)
			.capacity(capacity)
			.requestedCount(0)
			.memberCount(0)
			.introduce(introduce)
			.information(information)
			.roomLanguageMappings(new ArrayList<>())
			.roomUserMappings(new ArrayList<>())
			.build();
	}
}