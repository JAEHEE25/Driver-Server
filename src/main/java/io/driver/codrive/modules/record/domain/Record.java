package io.driver.codrive.modules.record.domain;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.global.BaseEntity;
import io.driver.codrive.modules.mappings.recordCategoryMapping.domain.RecordCategoryMapping;
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
	private Integer level;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Platform platform;

	@Column(nullable = false)
	private String problemUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Codeblock> codeblocks;

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RecordCategoryMapping> recordCategoryMappings;

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changeLevel(Integer level) {
		this.level = level;
	}

	public void changePlatform(Platform platform) {
		this.platform = platform;
	}

	public void changeProblemUrl(String problemUrl) {
		this.problemUrl = problemUrl;
	}

	public void changeCodeblocks(List<Codeblock> codeblocks) {
		this.codeblocks = codeblocks;
	}

	public void changeCategories(List<RecordCategoryMapping> recordCategoryMapping) {
		this.recordCategoryMappings.clear();
		this.recordCategoryMappings.addAll(recordCategoryMapping);
	}

	public void deleteCategories(List<RecordCategoryMapping> recordCategoryMapping) {
		this.recordCategoryMappings.removeAll(recordCategoryMapping);
	}

	public List<String> getCategories() {
		List<String> categories = new ArrayList<>();
		recordCategoryMappings.forEach(mapping -> {
			categories.add(mapping.getCategoryName());
		});
		return categories;
	}

}
