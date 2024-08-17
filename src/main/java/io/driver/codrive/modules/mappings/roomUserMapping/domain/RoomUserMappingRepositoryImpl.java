package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.mappings.roomUserMapping.domain.QRoomUserMapping.roomUserMapping;
import static io.driver.codrive.modules.room.domain.QRoom.room;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomStatus;

@Repository
public class RoomUserMappingRepositoryImpl extends QuerydslRepositorySupport implements RoomUserMappingRepositoryCustom {
	public RoomUserMappingRepositoryImpl() {
		super(RoomUserMapping.class);
	}

	@Override
	public Page<Room> getRoomsByUserAndRoomStatus(Long userId, RoomStatus roomStatus, Pageable pageable) {
		List<Room> rooms;
		if (roomStatus == null) {
			rooms = from(roomUserMapping)
				.select(roomUserMapping.room)
				.join(roomUserMapping.room, room)
				.where(roomUserMapping.user.userId.eq(userId))
				.fetch();
		} else {
			rooms = from(roomUserMapping)
				.select(roomUserMapping.room)
				.join(roomUserMapping.room, room)
				.where(roomUserMapping.user.userId.eq(userId), room.roomStatus.eq(roomStatus))
				.fetch();
		}
		long total = rooms.size();
		return new PageImpl<>(rooms, pageable, total);
	}
}

