package io.driver.codrive.modules.follow.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.model.SortType;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.follow.domain.FollowRepository;
import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.follow.model.response.FollowingSummaryListResponse;
import io.driver.codrive.modules.follow.model.response.FollowingWeeklyCountResponse;
import io.driver.codrive.modules.follow.model.response.TodaySolvedFollowingResponse;
import io.driver.codrive.modules.follow.model.response.WeeklyFollowingResponse;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.service.RoomService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserListResponse;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private static final int TODAY_SOLVED_FOLLOWINGS_SIZE = 3;
	private static final int WEEKLY_FOLLOWINGS = 3;
	private static final int FOLLOWINGS_SIZE = 10;
	private final UserService userService;
	private final RoomService roomService;
	private final FollowRepository followRepository;

	@Transactional
	public void follow(String nickname) {
		User target = userService.getUserByNickname(nickname);
		User currentUser = userService.getUserById(AuthUtils.getCurrentUserId());

		if (target.equals(currentUser)) {
			throw new IllegalArgumentApplicationException("자기 자신을 팔로우할 수 없습니다.");
		}

		if (getFollowByUsers(target, currentUser) != null) {
			throw new AlreadyExistsApplicationException("팔로우 데이터");
		}

		Follow follow = Follow.toFollow(target, currentUser);
		currentUser.addFollowing(follow);
		target.addFollower(follow);
		followRepository.save(follow);
	}

	@Transactional
	public void cancelFollow(String nickname) {
		User target = userService.getUserByNickname(nickname);
		User currentUser = userService.getUserById(AuthUtils.getCurrentUserId());

		Follow follow = getFollowByUsers(target, currentUser);
		if (follow == null) {
			throw new NotFoundApplcationException("팔로우 데이터");
		}
		followRepository.delete(follow);
		target.deleteFollower(follow);
		currentUser.deleteFollowing(follow);
	}

	private Follow getFollowByUsers(User following, User follower) {
		return followRepository.findByFollowingAndFollower(following, follower).orElse(null);
	}

	public UserListResponse getRandomUsers() {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<User> randomUsers = userService.getRandomUsersExceptMeAndFollowings(user);
		return UserListResponse.of(randomUsers, user);
	}

	@Transactional
	public FollowingWeeklyCountResponse getFollowingsWeeklyCount() {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<FollowingWeeklyCountResponse.WeeklyCountResponse> followingsWeeklyCount = user.getFollowings()
			.stream().map(follow -> {
				User following = follow.getFollowing();
				return getWeeklyCountResponse(following);
			}).toList();
		return FollowingWeeklyCountResponse.of(followingsWeeklyCount);
	}

	@Transactional
	protected FollowingWeeklyCountResponse.WeeklyCountResponse getWeeklyCountResponse(User following) {
		String nickname = following.getNickname();
		int count = userService.getThisWeekRecordsCount(following);
		return FollowingWeeklyCountResponse.WeeklyCountResponse.of(nickname, count);
	}

	@Transactional
	public TodaySolvedFollowingResponse getTodaySolvedFollowings(Integer page) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<User> followings = user.getFollowings().stream().map(Follow::getFollowing).toList();
		List<User> todaySolvedFollowings = followings.stream()
			.filter(following -> userService.getTodayRecordCount(following) > 0)
			.toList();

		Pageable pageable = PageRequest.of(page, TODAY_SOLVED_FOLLOWINGS_SIZE);
		Page<User> todaySolvedFollowingsByPage = PageUtils.getPage(todaySolvedFollowings, pageable, todaySolvedFollowings.size());
		return TodaySolvedFollowingResponse.of(todaySolvedFollowingsByPage.getTotalPages(), todaySolvedFollowingsByPage.getContent());
	}

	@Transactional
	public WeeklyFollowingResponse getWeeklyFollowings() {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<User> followings = user.getFollowings().stream().map(Follow::getFollowing).limit(WEEKLY_FOLLOWINGS).toList();
		return WeeklyFollowingResponse.of(followings);
	}

	@Transactional
	public FollowingSummaryListResponse getFollowingsSummary(SortType sortType, Integer page, Long roomId) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<Follow> followings = followRepository.getFollowings(user.getUserId(), sortType);
		List<User> followingsByRoom = getFollowingsByRoom(followings, roomId);

		Pageable pageable = PageRequest.of(page, FOLLOWINGS_SIZE);
		Page<User> followingsByPage = PageUtils.getPage(followingsByRoom, pageable, followingsByRoom.size());

		return FollowingSummaryListResponse.of(followingsByPage.getTotalPages(), followingsByPage.getContent());
	}

	@Transactional
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
