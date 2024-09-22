package io.driver.codrive.modules.room.domain;

import java.time.LocalDateTime;
import java.util.List;

import io.driver.codrive.global.entity.BaseEntity;
import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMapping;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
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

	@Column(nullable = false, length = 20)
	private String title;

	@Column(length = 20)
	private String password;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String imageSrc;

	@Column(nullable = false)
	private Integer capacity;

	@Column(nullable = false)
	private Integer requestedCount;

	@Column(nullable = false)
	private Integer memberCount;

	@Column(nullable = false, length = 60)
	private String introduce;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String information;

	@Column(nullable = false)
	private String uuid;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RoomStatus roomStatus;

	@Column(nullable = false)
	private LocalDateTime lastUpdatedAt;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomLanguageMapping> roomLanguageMappings;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomUserMapping> roomUserMappings;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomRequest> roomRequests;

	public boolean isPublicRoom() {
		return password == null || password.isEmpty();
	}

	public boolean isFull() {
		return memberCount >= capacity;
	}

	public boolean isCorrectPassword(String requestPassword) {
		return password.equals(requestPassword);
	}

	public boolean compareStatus(RoomStatus status) {
		return roomStatus == status;
	}

	public boolean existUserRequest(User user) {
		return roomRequests.stream().anyMatch(request -> request.getUser().equals(user));
	}

	public boolean hasMember(User user) {
		return roomUserMappings.stream().anyMatch(mapping -> mapping.getUser().equals(user));
	}

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void changeImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public void changeRequestedCount(Integer requestedCount) {
		this.requestedCount = requestedCount;
	}

	public void changeMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

	public void changeCapacity(Integer capacity) {
		if (capacity < memberCount) {
			throw new IllegalArgumentApplicationException("모집 인원은 현재 인원보다 적을 수 없습니다.");
		}
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

	public void changeRoomStatus(RoomStatus roomStatus) {
		this.roomStatus = roomStatus;
	}

	public void addRoomUserMappings(RoomUserMapping roomUserMapping) {
		this.roomUserMappings.add(roomUserMapping);
	}

	public void changeLastUpdatedAt(LocalDateTime lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public void addRoomRequests(RoomRequest roomRequest) {
		this.roomRequests.add(roomRequest);
	}

	public List<String> getLanguages() {
		return roomLanguageMappings.stream().map(RoomLanguageMapping::getLanguageName).toList();
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
