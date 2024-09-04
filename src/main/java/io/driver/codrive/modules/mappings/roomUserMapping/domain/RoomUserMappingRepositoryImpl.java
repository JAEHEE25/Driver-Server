package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.mappings.roomUserMapping.domain.QRoomUserMapping.roomUserMapping;
import static io.driver.codrive.modules.room.domain.QRoom.room;
import static io.driver.codrive.modules.user.domain.QUser.*;

import java.util.List;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.model.LanguageMemberCountDto;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomStatus;

@Repository
public class RoomUserMappingRepositoryImpl extends QuerydslRepositorySupport implements RoomUserMappingRepositoryCustom {
	public RoomUserMappingRepositoryImpl() {
		super(RoomUserMapping.class);
	}

	@Override
	public Page<Room> getRoomsByUserAndRoomStatusExcludingOwnByPage(Long userId, RoomStatus roomStatus, SortType sortType, Pageable pageable) {
		JPQLQuery<Room> query = from(roomUserMapping)
			.select(roomUserMapping.room)
			.join(roomUserMapping.room, room)
			.where(roomUserMapping.user.userId.eq(userId), room.owner.userId.ne(userId))
			.orderBy(createRoomOrderSpecifier(sortType));
		if (roomStatus != null) {
			query.where(room.roomStatus.eq(roomStatus));
		}
		List<Room> rooms = query.fetch();
		return PageUtils.getPage(rooms, pageable, rooms.size());
	}

	@Override
	public List<LanguageMemberCountDto> getLanguageMemberCount(Room room) {
		return from(roomUserMapping)
			.join(roomUserMapping.user, user)
			.where(roomUserMapping.room.eq(room))
			.groupBy(user.language)
			.select(Projections.fields(LanguageMemberCountDto.class,
				user.language.name.as("language"),
				user.count().as("count")))
			.fetch();
	}

	private OrderSpecifier createRoomOrderSpecifier(SortType sortType) {
		if (sortType == SortType.NEW) {
			return new OrderSpecifier<>(Order.DESC, roomUserMapping.createdAt);
		} else if (sortType == SortType.DICT) {
			return new OrderSpecifier<>(Order.ASC, room.title);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
    }
}

