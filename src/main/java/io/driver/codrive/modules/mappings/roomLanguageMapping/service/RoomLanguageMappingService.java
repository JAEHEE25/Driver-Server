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
	public void createRoomLanguageMapping(List<RoomLanguageMapping> mappings, Room room) {
		roomLanguageMappingRepository.saveAll(mappings);
		room.changeRoomLanguageMappings(mappings);
	}

	@Transactional
	public void deleteRoomLanguageMapping(List<RoomLanguageMapping> mappings) {
		roomLanguageMappingRepository.deleteAll(mappings);
	}

	public List<RoomLanguageMapping> getRoomLanguageMappingsByRequest(List<String> requestLanguages, Room room) {
		List<RoomLanguageMapping> roomLanguageMappings = new ArrayList<>();
		requestLanguages.forEach(request -> {
			Language language = languageService.getLanguageByName(request);
			roomLanguageMappings.add(RoomLanguageMapping.toEntity(room, language));
		});
		return roomLanguageMappings;
	}
}