package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import io.driver.codrive.global.entity.BaseEntity;
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
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	public static RoomUserMapping toRoomUserMapping(Room room, User user) {
		return RoomUserMapping.builder()
			.user(user)
			.room(room)
			.build();
	}

	public boolean isOwner() {
		return this.room.getOwner().getUserId().equals(this.user.getUserId());
	}

	@Override
	public Long getOwnerId() {
		return this.user.getUserId();
	}
}
