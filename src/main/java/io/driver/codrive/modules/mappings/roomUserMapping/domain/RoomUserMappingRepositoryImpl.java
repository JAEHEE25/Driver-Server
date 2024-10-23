package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.mappings.roomUserMapping.domain.QRoomUserMapping.roomUserMapping;
import static io.driver.codrive.modules.record.domain.QRecord.*;
import static io.driver.codrive.modules.room.domain.QRoom.room;
import static io.driver.codrive.modules.user.domain.QUser.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.model.LanguageMemberCountDto;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomStatus;
import io.driver.codrive.modules.user.domain.User;

@Repository
public class RoomUserMappingRepositoryImpl extends QuerydslRepositorySupport implements RoomUserMappingRepositoryCustom {
	private static final int ROOM_RANK_LIMIT = 3;

	public RoomUserMappingRepositoryImpl() {
		super(RoomUserMapping.class);
	}

	@Override
	public Page<Room> getRoomsByUserAndRoomStatusExcludingOwnByPage(Long userId, RoomStatus roomStatus, SortType sortType, Pageable pageable) {
		JPQLQuery<Room> query = from(roomUserMapping)
			.select(roomUserMapping.room)
			.join(roomUserMapping.room, room)
			.where(roomUserMapping.user.userId.eq(userId), room.owner.userId.ne(userId))
			.orderBy(sortType.createRoomUserOrderSpecifier(sortType));
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

	@Override
	public List<User> getRoomMembers(Room room, SortType sortType) {
		return from(roomUserMapping)
			.where(roomUserMapping.room.eq(room))
			.orderBy(createRoomMembersOrderSpecifier(sortType))
			.fetch()
			.stream().map(RoomUserMapping::getUser).toList();
	}

	private OrderSpecifier createRoomMembersOrderSpecifier(SortType sortType) {
        JPQLQuery<LocalDateTime> recentRecordDate = JPAExpressions
            .select(record.createdAt.max().coalesce(LocalDateTime.MIN))
            .from(record)
            .where(record.user.eq(roomUserMapping.user), record.recordStatus.eq(RecordStatus.SAVED));

		if (sortType == SortType.NEW) {
			return new OrderSpecifier<>(Order.DESC, recentRecordDate);
		} else if (sortType == SortType.DICT) {
			return new OrderSpecifier<>(Order.ASC, user.nickname);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

	@Override
	public List<User> getRoomRank(Room room, LocalDate pivotDate) {
		LocalDateTime mondayDateTime = DateUtils.getMondayDateTime(pivotDate);
		LocalDateTime sundayDateTime = DateUtils.getSundayDateTime(pivotDate);

		return from(roomUserMapping)
			.leftJoin(record).on(roomUserMapping.user.userId.eq(record.user.userId))
			.where(roomUserMapping.room.eq(room),
				record.createdAt.between(mondayDateTime, sundayDateTime),
				record.recordStatus.eq(RecordStatus.SAVED))
			.groupBy(roomUserMapping.user)
			.orderBy(record.count().desc())
			.limit(ROOM_RANK_LIMIT)
			.select(roomUserMapping.user)
			.fetch();
	}
}

