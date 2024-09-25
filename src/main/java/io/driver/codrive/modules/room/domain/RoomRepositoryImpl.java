package io.driver.codrive.modules.room.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

import static io.driver.codrive.modules.mappings.roomLanguageMapping.domain.QRoomLanguageMapping.roomLanguageMapping;
import static io.driver.codrive.modules.mappings.roomUserMapping.domain.QRoomUserMapping.*;
import static io.driver.codrive.modules.room.domain.QRoom.room;
import static io.driver.codrive.modules.language.domain.QLanguage.language;

import io.driver.codrive.global.model.SortType;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.room.model.dto.RoomFilterDto;

@Repository
public class RoomRepositoryImpl extends QuerydslRepositorySupport implements RoomRepositoryCustom {
	private static final int RANDOM_ROOM_SIZE = 6;
	private static final int DEFAULT_MIN_CAPACITY = 0;
	private static final int DEFAULT_MAX_CAPACITY = 50;

	public RoomRepositoryImpl() {
		super(Room.class);
	}

	@Override
	public List<Room> getRoomsByLanguageExcludingJoinedRoom(Long languageId, Long userId) {
		return from(room)
			.join(room.roomLanguageMappings, roomLanguageMapping)
			.join(roomLanguageMapping.language, language)
			.where(
				language.languageId.eq(languageId).and(room.roomStatus.eq(RoomStatus.ACTIVE))
					.and(room.roomId.notIn(
						JPAExpressions
							.select(roomUserMapping.room.roomId)
							.from(roomUserMapping)
							.where(roomUserMapping.user.userId.eq(userId))
					))
			)
			.orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
			.limit(RANDOM_ROOM_SIZE)
			.fetch();
	}

	@Override
	public Page<Room> filterRooms(RoomFilterDto roomFilterDto, Pageable pageable, SortType sortType) {
		JPQLQuery<Room> query = from(roomLanguageMapping)
			.where(getRoomFilterRequest(roomFilterDto))
			.orderBy(sortType.createRoomOrderSpecifier(sortType))
			.select(roomLanguageMapping.room);

		if (!roomFilterDto.tagIds().isEmpty()) {
			query.groupBy(roomLanguageMapping.room)
			.having(roomLanguageMapping.language.countDistinct().eq((long) roomFilterDto.tagIds().size()));
		}
		List<Room> rooms = query.fetch();
		return PageUtils.getPage(rooms, pageable, rooms.size());
	}

	private Predicate getRoomFilterRequest(RoomFilterDto roomFilterDto) {
		List<Long> tagIds = roomFilterDto.tagIds();
		Integer min = roomFilterDto.min();
		Integer max = roomFilterDto.max();
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!tagIds.isEmpty()) {
			booleanBuilder.and(roomLanguageMapping.language.languageId.in(tagIds));
		}
		if (min == null) {
			min = DEFAULT_MIN_CAPACITY;
		}
		if (max == null) {
			max = DEFAULT_MAX_CAPACITY;
		}

		booleanBuilder.and(room.capacity.between(min, max));
		return booleanBuilder;
	}

}