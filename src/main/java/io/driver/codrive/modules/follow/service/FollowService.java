package io.driver.codrive.modules.follow.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.follow.domain.FollowRepository;
import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.modules.follow.model.response.FollowingSummaryListResponse;
import io.driver.codrive.modules.follow.model.response.FollowingWeeklyCountResponse;
import io.driver.codrive.modules.follow.model.response.TodaySolvedFollowingResponse;
import io.driver.codrive.modules.follow.model.response.WeeklyFollowingResponse;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.service.NotificationService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.service.RecordService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.service.RoomService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserListResponse;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private static final int WEEKLY_FOLLOWINGS = 3;
	private static final int FOLLOWINGS_SIZE = 10;
	private final UserService userService;
	private final RoomService roomService;
	private final RecordService recordService;
	private final NotificationService notificationService;
	private final FollowRepository followRepository;

	@Transactional
	public void follow(Long currentUserId, String nickname) {
		User target = userService.getUserByNickname(nickname);
		User currentUser = userService.getUserById(currentUserId);

		if (target.equals(currentUser)) {
			throw new IllegalArgumentApplicationException("자기 자신을 팔로우할 수 없습니다.");
		}

		if (followRepository.existsByFollowingAndFollower(target, currentUser)) {
			throw new AlreadyExistsApplicationException("팔로우 데이터");
		}

		Follow follow = Follow.toFollow(target, currentUser);

		try {
			followRepository.save(follow);
			currentUser.addFollowing(follow);
			target.addFollower(follow);
		} catch (DataIntegrityViolationException e) {
			throw new InternalServerErrorApplicationException("팔로우할 수 없습니다.");
		}

		notificationService.sendFollowNotification(target, currentUserId, NotificationType.FOLLOW, currentUser.getNickname());
	}

	@Transactional
	public void cancelFollow(Long currentUserId, String nickname) {
		User target = userService.getUserByNickname(nickname);
		User currentUser = userService.getUserById(currentUserId);

		Follow follow = getFollowByUsers(target, currentUser);
		if (follow == null) {
			throw new NotFoundApplicationException("팔로우 데이터");
		}
		followRepository.delete(follow);
		target.deleteFollower(follow);
		currentUser.deleteFollowing(follow);
	}

	private Follow getFollowByUsers(User following, User follower) {
		return followRepository.findByFollowingAndFollower(following, follower).orElse(null);
	}

	@Transactional(readOnly = true)
	public UserListResponse getRandomUsers(Long userId) {
		User user = userService.getUserById(userId);
		List<User> randomUsers = userService.getRandomUsersExcludingMeAndFollowings(userId);
		return UserListResponse.of(randomUsers, user);
	}

	@Transactional(readOnly = true)
	public FollowingWeeklyCountResponse getFollowingsWeeklyCount(Long userId) {
		User user = userService.getUserById(userId);
		List<FollowingWeeklyCountResponse.WeeklyCountResponse> followingsWeeklyCount = user.getFollowings()
			.stream().map(follow -> {
				User following = follow.getFollowing();
				return getWeeklyCountResponse(following);
			}).toList();
		return FollowingWeeklyCountResponse.of(followingsWeeklyCount);
	}

	@Transactional
	protected FollowingWeeklyCountResponse.WeeklyCountResponse getWeeklyCountResponse(User following) {
		int count = recordService.getRecordsCountByWeek(following.getUserId(), LocalDate.now());
		return FollowingWeeklyCountResponse.WeeklyCountResponse.of(following.getNickname(), count);
	}

	@Transactional(readOnly = true)
	public TodaySolvedFollowingResponse getTodaySolvedFollowings(Long userId) {
		User currentUser = userService.getUserById(userId);
		List<User> followingsIncludeMe = getFollowingsIncludeMe(currentUser);
		List<User> todaySolvedFollowings = followingsIncludeMe.stream()
			.filter(user -> recordService.getTodayRecordCount(user) > 0)
			.sorted(getSolvedFollowingsComparator().reversed())
			.toList();
		return TodaySolvedFollowingResponse.of(todaySolvedFollowings);
	}

	private List<User> getFollowingsIncludeMe(User user) {
		List<User> followingsIncludeMe = new java.util.ArrayList<>(
			user.getFollowings().stream().map(Follow::getFollowing).toList());
		followingsIncludeMe.add(user);
		return followingsIncludeMe;
	}

	private Comparator<User> getSolvedFollowingsComparator() {
		return Comparator.comparing(user -> {
			List<Record> records = user.getRecords();
			if (records.isEmpty()) {
				return LocalDateTime.MIN;
			}
			Record recentRecord = records.get(records.size() - 1);
			return recentRecord.getCreatedAt();
		});
	}

	@Transactional(readOnly = true)
	public WeeklyFollowingResponse getWeeklyFollowings(Long userId) {
		User user = userService.getUserById(userId);
		List<User> followings = user.getFollowings()
			.stream()
			.map(Follow::getFollowing)
			.sorted(getSolvedFollowingsComparator().reversed())
			.limit(WEEKLY_FOLLOWINGS)
			.toList();
		return WeeklyFollowingResponse.of(followings);
	}

	@Transactional(readOnly = true)
	public FollowingSummaryListResponse getFollowingsSummary(Long userId, SortType sortType, Integer page, Long roomId) {
		List<Follow> followings = followRepository.getFollowings(userId, sortType);
		List<User> followingsByRoom = getFollowingsByRoom(followings, roomId);

		Pageable pageable = PageRequest.of(page, FOLLOWINGS_SIZE);
		Page<User> followingsByPage = PageUtils.getPage(followingsByRoom, pageable, followingsByRoom.size());

		return FollowingSummaryListResponse.of(followingsByPage.getTotalPages(), followingsByPage.getContent());
	}

	@Transactional(readOnly = true)
	protected List<User> getFollowingsByRoom(List<Follow> followings, Long groupId) {
		if (groupId == null) {
			return followings.stream().map(Follow::getFollowing).toList();
		} else {
			Room room = roomService.getRoomById(groupId);
			return followings.stream()
				.map(Follow::getFollowing)
				.filter(room::hasMember)
				.toList();
		}
	}
}
