package io.driver.codrive.modules.mappings.roomLanguageMapping.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMapping;
import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMappingRepository;
import io.driver.codrive.modules.room.domain.Room;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomLanguageMappingService {
	private final LanguageService languageService;
	private final RoomLanguageMappingRepository roomLanguageMappingRepository;

	@Transactional
	public void createRoomLanguageMapping(List<String> tags, Room room) {
		List<RoomLanguageMapping> mappings = getRoomLanguageMappingsByTag(tags, room);
		roomLanguageMappingRepository.saveAll(mappings);
		room.changeLanguages(mappings);
	}

	@Transactional
	public void deleteRoomLanguageMapping(List<RoomLanguageMapping> mappings, Room room) {
		roomLanguageMappingRepository.deleteAll(mappings);
		room.deleteLanguages(mappings);
	}

	public List<RoomLanguageMapping> getRoomLanguageMappingsByTag(List<String> tags, Room room) {
		List<RoomLanguageMapping> roomLanguageMappings = new ArrayList<>();
		tags.forEach(tag -> {
			Language language = languageService.getLanguageByName(tag);
			roomLanguageMappings.add(RoomLanguageMapping.toRoomLanguageMapping(room, language));
		});
		return roomLanguageMappings;
	}

}