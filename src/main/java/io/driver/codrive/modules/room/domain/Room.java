package io.driver.codrive.modules.room.domain;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.BaseEntity;
import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMapping;
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
public class Room extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = false)
	private Integer capacity;

	@Column(nullable = false)
	private String introduction;

	@Column(nullable = false)
	private String information;

	private String password;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User user;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RoomLanguageMapping> roomLanguageMappings;

	@Transactional
	public void addRoomLanguageMapping(RoomLanguageMapping roomLanguageMapping) {
		this.roomLanguageMappings.add(roomLanguageMapping);
	}
}
