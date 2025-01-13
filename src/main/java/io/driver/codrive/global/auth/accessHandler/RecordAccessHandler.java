package io.driver.codrive.global.auth.accessHandler;

import org.springframework.stereotype.Component;

import io.driver.codrive.modules.record.service.RecordService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecordAccessHandler implements EntityAccessHandler {

	private final RecordService recordService;

	@Override
	public boolean isOwner(Long recordId) {
		Long ownerId = recordService.getOwnerIdByRecordId(recordId);
		return isSameWithCurrentUserId(ownerId);
	}
}
