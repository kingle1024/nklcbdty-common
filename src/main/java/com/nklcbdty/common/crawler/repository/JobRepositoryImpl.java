package com.nklcbdty.common.crawler.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.nklcbdty.common.vo.Job_mst;
import com.nklcbdty.common.vo.QJob_mst;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

// Spring Data 의 *Impl 컨벤션으로 JobRepository 가 자동 결합.
public class JobRepositoryImpl extends QuerydslRepositorySupport implements JobRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public JobRepositoryImpl(EntityManager entityManager) {
        super(Job_mst.class);
        this.queryFactory = new JPAQueryFactory(Objects.requireNonNull(entityManager));
    }

    @Override
    public List<Job_mst> findActiveJobs() {
        QJob_mst job_mst = QJob_mst.job_mst;

        DateTimeExpression<LocalDateTime> jobEndDateAsDateTimeExpression = Expressions.dateTimeTemplate(
            LocalDateTime.class,
            "STR_TO_DATE({0}, {1})",
            job_mst.endDate,
            ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );
        String todayString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime todayStart = LocalDate.parse(todayString).atStartOfDay();

        return queryFactory
                .selectFrom(job_mst)
                .where(
                    job_mst.jobDetailLink.isNotNull(),
                    job_mst.annoSubject.isNotNull(),
                    job_mst.endDate.isNull()
                        .or(job_mst.endDate.eq("영입종료시"))
                        .or(job_mst.endDate.eq("영업종료시"))
                        .or(jobEndDateAsDateTimeExpression.goe(todayStart))
                )
                .fetch();
    }

    @Override
    public List<Job_mst> findJobsByDetailedCriteria(
        List<String> companyCds,
        List<String> subJobCdNms,
        Long personalHistoryStart,
        Long personalHistoryEnd
    ) {
        QJob_mst job_mst = QJob_mst.job_mst;

        return queryFactory
                .selectFrom(job_mst)
                .where(
                    companyCdIn(companyCds),
                    subJobCdNmIn(subJobCdNms),
                    personalHistoryRange(job_mst, personalHistoryStart, personalHistoryEnd),
                    endDateAfterToday(job_mst)
                )
                .orderBy(job_mst.endDate.desc())
                .fetch();
    }

    private BooleanExpression endDateAfterToday(QJob_mst job_mst) {
        DateTimeExpression<LocalDateTime> jobEndDateAsDateTimeExpression = Expressions.dateTimeTemplate(
            LocalDateTime.class,
            "STR_TO_DATE({0}, {1})",
            job_mst.endDate,
            ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );
        String todayString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime todayStart = LocalDate.parse(todayString).atStartOfDay();

        return job_mst.endDate
            .isNull()
            .or(job_mst.endDate.eq("영입종료시"))
            .or(jobEndDateAsDateTimeExpression.goe(todayStart));
    }

    private BooleanExpression companyCdIn(List<String> companyCds) {
        if (companyCds == null || companyCds.isEmpty()) {
            return null;
        }
        return QJob_mst.job_mst.companyCd.in(companyCds);
    }

    private BooleanExpression subJobCdNmIn(List<String> subJobCdNms) {
        if (subJobCdNms == null || subJobCdNms.isEmpty()) {
            return null;
        }
        return QJob_mst.job_mst.subJobCdNm.in(subJobCdNms);
    }

    // 사용자 경력 필터. (0L, 0L) = 모든 경력. 그 외엔 경력 무관 공고 제외 + 범위 매칭.
    private BooleanExpression personalHistoryRange(
        QJob_mst job,
        Long userFilterMinExp,
        Long userFilterMaxExp
    ) {
        if (userFilterMinExp != null && userFilterMinExp == 0L &&
            userFilterMaxExp != null && userFilterMaxExp == 0L) {
            return null;
        }

        BooleanExpression excludeNoExperienceJob =
            job.personalHistory.gt(0L)
            .or(job.personalHistoryEnd.gt(0L));

        BooleanExpression condMinCareerMatches =
            job.personalHistory.loe(userFilterMinExp);

        BooleanExpression userMinExpIsZero;
        if (userFilterMinExp != null && userFilterMinExp == 0L) {
            userMinExpIsZero = Expressions.TRUE;
        } else {
            userMinExpIsZero = Expressions.FALSE;
        }
        condMinCareerMatches = condMinCareerMatches.or(userMinExpIsZero);

        BooleanExpression condMaxCareerMatches =
            job.personalHistoryEnd.eq(0L)
            .or(job.personalHistoryEnd.goe(userFilterMaxExp));

        BooleanExpression userMaxExpIsZero;
        if (userFilterMaxExp != null && userFilterMaxExp == 0L) {
            userMaxExpIsZero = Expressions.TRUE;
        } else {
            userMaxExpIsZero = Expressions.FALSE;
        }
        condMaxCareerMatches = condMaxCareerMatches.or(userMaxExpIsZero);

        return excludeNoExperienceJob
                .and(condMinCareerMatches)
                .and(condMaxCareerMatches);
    }
}
