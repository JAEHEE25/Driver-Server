package io.driver.codrive.modules.record.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.record.domain.QRecord.record;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.model.RecordCountDto;

@Repository
public class RecordRepositoryImpl extends QuerydslRepositorySupport implements RecordRepositoryCustom {
	public RecordRepositoryImpl() {
		super(Record.class);
	}

	@Override
	public List<Record> getDailyRecords(Long userId, LocalDate pivotDate) {
		StringTemplate formattedDate = getFormattedDate("%Y-%m-%d");
		return from(record)
			.where(record.user.userId.eq(userId), formattedDate.eq(pivotDate.toString()),
				record.recordStatus.eq(RecordStatus.SAVED))
			.orderBy(record.createdAt.desc())
			.fetch();
	}

	@Override
	public Page<Record> getMonthlyRecords(Long userId, LocalDate pivotDate, Pageable pageable) {
		StringTemplate formattedYearMonth = getFormattedDate("%Y-%m");
		String pivotDateYearMonth = DateUtils.formatYearMonth(pivotDate);
		List<Record> records = from(record)
			.where(record.user.userId.eq(userId), formattedYearMonth.eq(pivotDateYearMonth),
				record.recordStatus.eq(RecordStatus.SAVED))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(record.createdAt.desc())
			.fetch();

		long total = from(record)
			.where(record.user.userId.eq(userId), formattedYearMonth.eq(pivotDateYearMonth),
				record.recordStatus.eq(RecordStatus.SAVED))
			.fetchCount();

		return new PageImpl<>(records, pageable, total);
	}

	@Override
	public List<RecordCountDto> getYearlyRecordCount(Long userId, LocalDate pivotDate) {
		StringTemplate formattedYear = getFormattedDate("%Y");
		StringTemplate formattedMonth = getFormattedDate("%c");
		String pivotDateYear = DateUtils.formatYear(pivotDate);

		return from(record)
			.where(record.user.userId.eq(userId), formattedYear.eq(pivotDateYear),
				record.recordStatus.eq(RecordStatus.SAVED))
			.groupBy(formattedMonth)
			.select(Projections.fields(RecordCountDto.class,
				formattedMonth.as("date"),
				record.count().as("count")))
			.fetch();
	}

	@Override
	public List<RecordCountDto> getMonthlyRecordCountBoard(Long userId, LocalDate pivotDate) {
		StringTemplate formattedYearMonth = getFormattedDate("%Y-%m");
		StringTemplate formattedDay = getFormattedDate("%e");
		String pivotDateYearMonth = DateUtils.formatYearMonth(pivotDate);

		return from(record)
			.where(record.user.userId.eq(userId), formattedYearMonth.eq(pivotDateYearMonth),
				record.recordStatus.eq(RecordStatus.SAVED))
			.groupBy(formattedDay)
			.select(Projections.fields(RecordCountDto.class,
				formattedDay.as("date"),
				record.count().as("count")))
			.fetch();
	}

	@Override
	public List<RecordCountDto> getWeeklyRecordCountBoard(Long userId,
		LocalDate pivotDate) { //월요일 00:00:00부터 일요일 23:59:59까지
		LocalDateTime mondayDateTime = getMondayDateTime(pivotDate);
		LocalDateTime sundayDateTime = getSundayDateTime(pivotDate);
		StringTemplate formattedDay = getFormattedDate("%e");

		return from(record)
			.where(record.user.userId.eq(userId), record.createdAt.between(mondayDateTime, sundayDateTime),
				record.recordStatus.eq(RecordStatus.SAVED))
			.groupBy(formattedDay)
			.select(Projections.fields(RecordCountDto.class,
				formattedDay.as("date"),
				record.count().as("count")))
			.fetch();
	}

	public StringTemplate getFormattedDate(String format) {
		return Expressions.stringTemplate("DATE_FORMAT({0}, {1})", record.createdAt, ConstantImpl.create(format));
	}

	public LocalDateTime getMondayDateTime(LocalDate pivotDate) {
		int pivotDay = pivotDate.getDayOfWeek().getValue();
		int monday = DayOfWeek.MONDAY.getValue();
		return pivotDate.minusDays(pivotDay - monday).atStartOfDay();
	}

	public LocalDateTime getSundayDateTime(LocalDate pivotDate) {
		int pivotDay = pivotDate.getDayOfWeek().getValue();
		int sunday = DayOfWeek.SUNDAY.getValue();
		return pivotDate.plusDays(sunday - pivotDay).atTime(23, 59, 59);
	}
}
