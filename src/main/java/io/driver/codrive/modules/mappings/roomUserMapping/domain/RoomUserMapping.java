package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import io.driver.codrive.modules.global.BaseEntity;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
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
public class RoomUserMapping extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomUserMappingId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private Room room;

	public static RoomUserMapping toEntity(Room room, User user) {
		return RoomUserMapping.builder()
			.user(user)
			.room(room)
			.build();
	}
}
