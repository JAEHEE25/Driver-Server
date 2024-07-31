package io.driver.codrive.modules.follow.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import io.driver.codrive.global.BaseEntity;
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
@SQLRestriction("canceled = false")
@SQLDelete(sql = "UPDATE follow SET canceled = true WHERE follow_id = ?")
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

	@Column(nullable = false)
	private Boolean canceled;

	public static Follow toEntity(User following, User follower) {
		return Follow.builder()
			.following(following)
			.follower(follower)
			.canceled(false)
			.build();
	}
}

