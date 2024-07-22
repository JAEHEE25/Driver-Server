package io.driver.codrive.modules.mappings.roomLanguageMapping.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

	public void createRoomLanguageMapping(List<String> languages, Room room) {
		languages.forEach(request -> {
			Language language = languageService.getLanguageByName(request);
			RoomLanguageMapping newMapping = RoomLanguageMapping.toEntity(language, room);
			roomLanguageMappingRepository.save(newMapping);
			room.addRoomLanguageMapping(newMapping);
		});
	}
}
