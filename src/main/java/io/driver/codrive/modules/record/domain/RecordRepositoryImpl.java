package io.driver.codrive.modules.record.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.record.domain.QRecord.record;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.model.BoardDetailDto;

@Repository
public class RecordRepositoryImpl extends QuerydslRepositorySupport implements RecordRepositoryCustom {
	public RecordRepositoryImpl() {
		super(Record.class);
	}

	@Override
	public List<Record> getSavedRecordsByDate(Long userId, LocalDate pivotDate) {
		StringTemplate formattedDate = getFormattedDate("%Y-%m-%d");
		return from(record)
			.where(record.user.userId.eq(userId), formattedDate.eq(pivotDate.toString()), record.status.eq(Status.SAVED))
			.fetch();
	}

	@Override
	public List<BoardDetailDto> getSavedRecordCountByMonth(Long userId, LocalDate pivotDate) {
		StringTemplate formattedYearMonth = getFormattedDate("%Y-%m");
		StringTemplate formattedDay = getFormattedDate("%e");
		String pivotDateYearMonth = DateUtils.formatYearMonth(pivotDate);

		return from(record)
			.where(record.user.userId.eq(userId), formattedYearMonth.eq(pivotDateYearMonth), record.status.eq(Status.SAVED))
			.groupBy(formattedDay)
			.select(Projections.fields(BoardDetailDto.class,
				formattedDay.as("date"),
				record.count().as("count")))
			.fetch();
	}

	@Override
	public List<BoardDetailDto> getSavedRecordCountByWeek(Long userId, LocalDate pivotDate) { //월요일 00:00:00부터 일요일 23:59:59까지
		LocalDateTime mondayDateTime = getMondayDateTime(pivotDate);
		LocalDateTime sundayDateTime = getSundayDateTime(pivotDate);
		StringTemplate formattedDate = getFormattedDate("%e");

		return from(record)
			.where(record.user.userId.eq(userId), record.createdAt.between(mondayDateTime, sundayDateTime), record.status.eq(Status.SAVED))
			.groupBy(formattedDate)
			.select(Projections.fields(BoardDetailDto.class,
				formattedDate.as("date"),
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
