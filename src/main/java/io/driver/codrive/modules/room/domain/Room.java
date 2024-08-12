package io.driver.codrive.modules.room.domain;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.global.entity.BaseEntity;
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
	private String imageSrc;

	@Column(nullable = false)
	private Integer capacity;

	@Column(nullable = false)
	private String introduce;

	@Column(nullable = false)
	private String information;

	@Column(nullable = false)
	private String uuid;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomLanguageMapping> roomLanguageMappings;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomUserMapping> roomUserMappings;

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void changeImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public void changeCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public void changeIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public void changeInformation(String information) {
		this.information = information;
	}

	public void changeLanguages(List<RoomLanguageMapping> mappings) {
		this.roomLanguageMappings.clear();
		this.roomLanguageMappings.addAll(mappings);
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

	public void deleteLanguages(List<RoomLanguageMapping> mappings) {
		roomLanguageMappings.removeAll(mappings);
	}

	@Override
	public Long getOwnerId() {
		return this.owner.getUserId();
	}
}
