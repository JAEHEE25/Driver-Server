package io.driver.codrive.modules.record.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.driver.codrive.modules.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.RecordListRequest;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final RecordRepository recordRepository;

	public List<Record> getRecordsByDate(User user, RecordListRequest request) {
		if (request.pivotDate() == null) {
			return recordRepository.findAllByUser(user);
		}
		return recordRepository.getRecordsByDate(user.getUserId(), DateUtils.getLocalDateByString(request.pivotDate()));
	}

}
