package io.driver.codrive.modules.language.domain;

import java.util.List;

import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMapping;
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
public class Language {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long languageId;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RoomLanguageMapping> roomLanguageMappings;

}
