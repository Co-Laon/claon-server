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

import static claon.center.domain.QCenterReview.centerReview;
import static claon.user.domain.QBlockUser.blockUser;
import static claon.user.domain.QUser.user;

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
                .join(centerReview.writer, user)
                .fetchJoin()
                .where(centerReview.center.id.eq(centerId)
                        .and(centerReview.writer.id.ne(userId))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<CenterReview> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<CenterReview> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable) {
        JPQLQuery<CenterReview> query = jpaQueryFactory
                .selectFrom(centerReview)
                .join(centerReview.writer, user)
                .fetchJoin()
                .where(centerReview.center.id.eq(centerId)
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

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
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ))
                .fetch()
                .get(0);
    }

    public Integer countByCenterExceptBlockUser(String centerId, String userId) {
        return (int) jpaQueryFactory
                .selectFrom(centerReview)
                .where(centerReview.center.id.eq(centerId)
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(centerReview.id.notIn(
                                JPAExpressions
                                        .select(centerReview.id)
                                        .from(centerReview)
                                        .join(blockUser).on(centerReview.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ))
                .fetchCount();
    }
}
