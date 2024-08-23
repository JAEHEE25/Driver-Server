package io.driver.codrive.modules.room.domain;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.Expressions;

import static io.driver.codrive.modules.mappings.roomLanguageMapping.domain.QRoomLanguageMapping.roomLanguageMapping;
import static io.driver.codrive.modules.room.domain.QRoom.room;
import static io.driver.codrive.modules.language.domain.QLanguage.language;

@Repository
public class RoomRepositoryImpl extends QuerydslRepositorySupport implements RoomRepositoryCustom {
	private static final int RANDOM_ROOM_SIZE = 6;
	public RoomRepositoryImpl() {
		super(Room.class);
	}

	@Override
	public List<Room> getRoomsByLanguageExcludingOwnRoom(Long languageId, Long userId) {
		return from(room)
                .join(room.roomLanguageMappings, roomLanguageMapping)
                .join(roomLanguageMapping.language, language)
				.where(language.languageId.eq(languageId), room.owner.userId.ne(userId))
                .orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
				.limit(RANDOM_ROOM_SIZE)
				.fetch();
	}
}
