package io.driver.codrive.modules.record.domain;

import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.global.BaseEntity;
import io.driver.codrive.modules.mappings.recordTagMapping.domain.RecordTagMapping;
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
public class Record extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recordId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private Integer difficulty;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Platform platform;

	@Column(nullable = false)
	private String problemUrl;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Codeblock> codeblocks;

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RecordTagMapping> recordTagMappings;

	public void changeTags(List<RecordTagMapping> recordTagMapping) {
		this.recordTagMappings = recordTagMapping;
	}
}
