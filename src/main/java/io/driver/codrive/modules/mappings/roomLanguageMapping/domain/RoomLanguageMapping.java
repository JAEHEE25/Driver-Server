package io.driver.codrive.modules.mappings.roomLanguageMapping.domain;

import io.driver.codrive.global.entity.BaseEntity;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.room.domain.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomLanguageMapping extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomLanguageMappingId;

	@ManyToOne
	@JoinColumn(name = "language_id", nullable = false)
	private Language language;

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	public static RoomLanguageMapping toRoomLanguageMapping(Room room, Language language) {
		return RoomLanguageMapping.builder()
			.language(language)
			.room(room)
			.build();
	}

	public String getLanguageName() {
		return language.getName();
	}
}