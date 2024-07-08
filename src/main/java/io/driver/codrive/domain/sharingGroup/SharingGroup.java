package io.driver.codrive.domain.sharingGroup;

import io.driver.codrive.domain.global.BaseEntity;
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
public class SharingGroup extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sharingGroupId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private Integer capacity;

	@Column(nullable = false)
	private String introduction;

	@Column(nullable = false)
	private String information;

	private String password;
}
