package io.driver.codrive.modules.mappings.recordTagMapping.domain;

import io.driver.codrive.modules.global.BaseEntity;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.tag.domain.Tag;
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
public class RecordTagMapping extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recordTagMappingId;

	@ManyToOne
	@JoinColumn(name = "record_id")
	private Record record;

	@ManyToOne
	@JoinColumn(name = "tag_id")
	private Tag tag;

	public static RecordTagMapping toEntity(Record record, Tag tag) {
		return RecordTagMapping.builder()
			.record(record)
			.tag(tag)
			.build();
	}

}
