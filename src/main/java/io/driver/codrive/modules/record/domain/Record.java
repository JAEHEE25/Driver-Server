package io.driver.codrive.modules.record.domain;

import io.driver.codrive.modules.global.BaseEntity;
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
public class Record extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recordId;

	@Column(nullable = false)
	private String problemTitle;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProblemPlatform problemPlatform;

	@Column(nullable = false)
	private String problemUrl;

	@Column(nullable = false)
	private Integer difficulty;

	@Column(nullable = false)
	private String solution;

	private String memo;
}
