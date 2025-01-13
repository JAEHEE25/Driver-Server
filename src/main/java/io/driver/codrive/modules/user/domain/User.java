package io.driver.codrive.modules.user.domain;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.global.entity.BaseEntity;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
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

	@Column(nullable = false, columnDefinition = "TEXT")
	private String profileImg;

	@Column(length = 30)
	private String comment;

	private String githubUrl;

	@Column(nullable = false)
	private String githubRepositoryName;

	@Column(nullable = false)
	private Integer goal;

	@Column(nullable = false)
	private Integer successRate;

	@Column(nullable = false)
	private Long solvedCount;

	@Column(nullable = false)
	private Boolean withdraw;

	@ManyToOne
	@JoinColumn(name = "language_id", nullable = false)
	private Language language;

	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Follow> followings;

	@OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Follow> followers;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomUserMapping> roomUserMappings;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Record> records;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Room> createdRooms;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RoomRequest> roomRequests;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Notification> notifications;

	public void addRecord(Record record) {
		this.records.add(record);
	}

	public void addRoomUserMappings(RoomUserMapping roomUserMapping) {
		this.roomUserMappings.add(roomUserMapping);
	}

	public void addFollowing(Follow follow) {
		this.followings.add(follow);
	}

	public void addFollower(Follow follow) {
		this.followers.add(follow);
	}

	public void addSolvedCount() {
		this.solvedCount++;
	}

	public void deleteFollowing(Follow follow) {
		this.followings.remove(follow);
	}

	public void deleteFollower(Follow follow) {
		this.followers.remove(follow);
	}

	public void changeUserName(String username) {
		this.username = username;
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

	public void changeGithubRepositoryName(String githubRepositoryName) {
		this.githubRepositoryName = githubRepositoryName;
	}

	public void changeLanguage(Language language) {
		this.language = language;
	}

	public void changeGoal(Integer goal) {
		this.goal = goal;
	}

	public void changeSuccessRate(Integer successRate) {
		this.successRate = successRate;
	}

	public void deleteJoinedRoom(RoomUserMapping mapping) {
		this.roomUserMappings.remove(mapping);
	}

	public Record getRecentProblem() {
		List<Record> savedRecords = records.stream().filter(Record::isSaved).toList();
		if (savedRecords.isEmpty()) return null;
		int lastIndex = savedRecords.size() - 1;
		return savedRecords.get(lastIndex);
	}

	public String getRecentProblemTitle() {
		Record recentProblem = getRecentProblem();
		if (recentProblem == null) return null;
		return recentProblem.getTitle();
	}

	public List<Room> getJoinedRooms() {
		return roomUserMappings.stream().map(RoomUserMapping::getRoom).toList();
	}

	public Boolean isFollowing(User target) {
		if (this.equals(target)) {
			return null;
		}
		return this.followings.stream().anyMatch(follow -> follow.getFollowing().equals(target));
	}
}