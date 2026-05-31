package com.nklcbdty.common.user.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nklcbdty.common.vo.QUserInterestVo;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserInterestRepositoryImpl implements UserInterestRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUserInterestVo userInterest = QUserInterestVo.userInterestVo;

    @Override
    public List<Tuple> findGroupedByUserTypeAndUserId() {
        return queryFactory
            .select(userInterest.itemType, userInterest.userId)
            .from(userInterest)
            .groupBy(userInterest.itemType, userInterest.userId)
            .fetch();
    }

    public List<Tuple> findUserCategories() {
        CaseBuilder caseBuilder = new CaseBuilder();

        NumberExpression<Integer> hasCompany = caseBuilder
                .when(userInterest.itemType.eq("company")).then(1)
                .otherwise(0)
                .max();

        NumberExpression<Integer> hasCareer = caseBuilder
                .when(userInterest.itemType.eq("job")).then(1)
                .otherwise(0)
                .max();

        Expression<String> userCategory = caseBuilder
                .when(hasCompany.eq(1).and(hasCareer.eq(1))).then("AB")
                .when(hasCompany.eq(1).and(hasCareer.eq(0))).then("onlyA")
                .when(hasCompany.eq(0).and(hasCareer.eq(1))).then("onlyB")
                .otherwise("nothing");

        return queryFactory
            .select(
                    userInterest.userId,
                    userCategory
            )
            .from(userInterest)
            .groupBy(userInterest.userId)
            .having(hasCompany.eq(1).or(hasCareer.eq(1)))
            .fetch();
    }
}
