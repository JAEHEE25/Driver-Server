package io.driver.codrive.modules.follow.domain;

import io.driver.codrive.global.entity.BaseEntity;
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
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long followId;

	@ManyToOne
	@JoinColumn(name = "following_id", nullable = false)
	private User following;

	@ManyToOne
	@JoinColumn(name = "follower_id", nullable = false)
	private User follower;

	public static Follow toFollow(User following, User follower) {
		return Follow.builder()
			.following(following)
			.follower(follower)
			.build();
	}

	@Override
	public Long getOwnerId() {
		return null;
	}
}

