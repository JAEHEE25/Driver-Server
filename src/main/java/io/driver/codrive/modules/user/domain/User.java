package io.driver.codrive.modules.user.domain;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.global.entity.BaseEntity;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.room.domain.Room;
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
@SQLRestriction("withdraw = false")
@SQLDelete(sql = "UPDATE user SET withdraw = true WHERE user_id = ?")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String profileImg;

	private String comment;

	private String githubUrl;

	@Column(nullable = false)
	private Integer level;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	private Boolean withdraw;

	@ManyToOne
	@JoinColumn(name = "language_id", nullable = false)
	private Language language;

	@OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Follow> followings;

	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Follow> followers;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomUserMapping> roomUserMappings;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Record> records;

	public void addRoomUserMappings(RoomUserMapping roomUserMapping) {
		this.roomUserMappings.add(roomUserMapping);
	}

	public void changeName(String name) {
		this.name = name;
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void changeProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}

	public void changeComment(String comment) {
		this.comment = comment;
	}

	public void changeGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public void changeLanguage(Language language) {
		this.language = language;
	}

	public void changeRole(Role role) {
		this.role = role;
	}

	public List<Room> getJoinedRooms() {
		return roomUserMappings.stream()
			.map(RoomUserMapping::getRoom)
			.toList();
	}

	public List<Room> getCreatedRooms() {
		return roomUserMappings.stream()
			.filter(RoomUserMapping::isOwner)
			.map(RoomUserMapping::getRoom)
			.toList();
	}

	public void deleteJoinedRoom(RoomUserMapping mapping) {
		this.roomUserMappings.remove(mapping);
	}

	@Override
	public Long getOwnerId() {
		return this.userId;
	}
}