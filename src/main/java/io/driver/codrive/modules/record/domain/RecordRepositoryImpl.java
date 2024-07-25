package io.driver.codrive.modules.record.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.record.domain.QRecord.record;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

@Repository
public class RecordRepositoryImpl extends QuerydslRepositorySupport implements RecordRepositoryCustom {
	public RecordRepositoryImpl() {
		super(Record.class);
	}

	@Override
	public List<Record> getRecordsByDate(Long userId, LocalDate pivotDate) {
		return from(record)
			.where(record.user.userId.eq(userId), getDateMatch(pivotDate))
			.fetch();
	}

	public BooleanExpression getDateMatch(LocalDate pivotDate) {
		return Expressions.dateTemplate(
				LocalDate.class,
				"DATE({0})",
				record.createdAt)
			.eq(pivotDate);
	}

}
