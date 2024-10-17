package io.driver.codrive.modules.roomRequest.domain;

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
public class RoomRequest extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomRequestId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRequestStatus userRequestStatus;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	public static RoomRequest toPublicRoomRequest(Room room, User user) {
		return RoomRequest.builder()
			.user(user)
			.room(room)
			.userRequestStatus(UserRequestStatus.REQUESTED)
			.build();
	}

	public static RoomRequest toPrivateRoomRequest(Room room, User user) {
		return RoomRequest.builder()
			.user(user)
			.room(room)
			.userRequestStatus(UserRequestStatus.JOINED)
			.build();
	}

	public boolean compareStatus(UserRequestStatus userRequestStatus) {
		return this.userRequestStatus == userRequestStatus;
	}

	public void changeRoomRequestStatus(UserRequestStatus userRequestStatus) {
		this.userRequestStatus = userRequestStatus;
	}

	@Override
	public Long getOwnerId() {
		return this.room.getOwnerId();
	}
}
