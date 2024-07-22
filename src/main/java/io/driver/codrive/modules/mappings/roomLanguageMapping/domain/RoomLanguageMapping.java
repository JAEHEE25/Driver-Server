package io.driver.codrive.modules.mappings.roomLanguageMapping.domain;

import io.driver.codrive.modules.global.BaseEntity;
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
	@JoinColumn(name = "language_id")
	private Language language;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private Room room;

	public static RoomLanguageMapping toEntity(Language language, Room room) {
		return RoomLanguageMapping.builder()
			.language(language)
			.room(room)
			.build();
	}
}