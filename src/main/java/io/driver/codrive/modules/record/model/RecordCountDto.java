package io.driver.codrive.modules.record.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordCountDto {
	private String date;
	private Long count;
}
