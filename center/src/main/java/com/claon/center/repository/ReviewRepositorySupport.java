package com.claon.center.repository;

import com.claon.center.domain.CenterReview;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.claon.center.domain.QBlockUser.blockUser;
import static com.claon.center.domain.QCenterReview.centerReview;

@Repository
public class ReviewRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public ReviewRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(CenterReview.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<CenterReview> findByCenterExceptBlockUserAndSelf(String centerId, String userId, Pageable pageable) {
        JPQLQuery<CenterReview> query = jpaQueryFactory
                .selectFrom(centerReview)
                .where(centerReview.center.id.eq(centerId)
                        .and(centerReview.writerId.ne(userId))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                );

        long totalCount = query.fetchCount();
        List<CenterReview> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Double findRankByCenterExceptBlockUser(String centerId, String userId) {
        return jpaQueryFactory
                .select(centerReview.rank.avg())
                .from(centerReview)
                .where(centerReview.center.id.eq(centerId)
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                )
                .fetch()
                .get(0);
    }

    public Long countByCenterExceptBlockUser(String centerId, String userId) {
        return jpaQueryFactory
                .select(centerReview.count())
                .from(centerReview)
                .where(centerReview.center.id.eq(centerId)
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                )
                .fetchOne();
    }
}
