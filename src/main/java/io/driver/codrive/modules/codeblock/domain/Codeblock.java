package io.driver.codrive.modules.codeblock.domain;

import io.driver.codrive.global.entity.BaseEntity;
import io.driver.codrive.modules.record.domain.Record;
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
public class Codeblock extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codeblockId;

	@Column(columnDefinition = "TEXT")
	private String code;

	@Column(columnDefinition = "TEXT")
	private String memo;

	@ManyToOne
	@JoinColumn(name = "record_id", nullable = false)
	private Record record;
}
