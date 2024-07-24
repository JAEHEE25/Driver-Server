package io.driver.codrive.modules.room.domain;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.modules.global.BaseEntity;
import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMapping;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
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
public class Room extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@Column(nullable = false)
	private String title;

	private String password;

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = false)
	private Integer capacity;

	@Column(nullable = false)
	private String introduction;

	@Column(nullable = false)
	private String information;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User owner;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RoomLanguageMapping> roomLanguageMappings;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RoomUserMapping> roomUserMappings;

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void changeImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void changeCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public void changeIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public void changeInformation(String information) {
		this.information = information;
	}

	public void changeLanguages(List<RoomLanguageMapping> mappings) {
		this.roomLanguageMappings = mappings;
	}

	public void addRoomUserMappings(RoomUserMapping roomUserMapping) {
		this.roomUserMappings.add(roomUserMapping);
	}

	public List<String> getLanguages() {
		List<String> languages = new ArrayList<>();
		roomLanguageMappings.forEach(mapping -> {
			languages.add(mapping.getLanguageName());
		});
		return languages;
	}

	public List<User> getRoomMembers() {
		List<User> users = new ArrayList<>();
		roomUserMappings.forEach(mapping -> {
			users.add(mapping.getUser());
		});
		return users;
	}

	public void deleteMember(RoomUserMapping mapping) {
		roomUserMappings.remove(mapping);
	}

}
