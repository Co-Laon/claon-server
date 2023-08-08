package com.claon.post.repository;

import com.claon.center.domain.Center;
import com.claon.post.domain.Post;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.claon.center.domain.QCenter.center;
import static com.claon.post.domain.QClimbingHistory.climbingHistory;
import static com.claon.post.domain.QPost.post;
import static com.claon.user.domain.QBlockUser.blockUser;
import static com.claon.user.domain.QLaon.laon1;
import static com.claon.user.domain.QUser.user;

@Repository
public class PostRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Post.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<Post> findLaonUserPostsExceptBlockUser(String userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime diff = now.minusMonths(3);

        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .join(post.writer, user)
                .fetchJoin()
                .join(laon1)
                .on(post.writer.id.eq(laon1.laon.id))
                .where(post.isDeleted.isFalse()
                        .and(post.createdAt.between(diff, now))
                        .and(user.isPrivate.isFalse())
                        .and(post.writer.id.ne(userId))
                        .and(laon1.user.id.eq(userId))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Post> findExceptLaonUserAndBlockUser(String userId, Pageable pageable) {
        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .join(post.writer, user)
                .fetchJoin()
                .where(post.isDeleted.isFalse()
                        .and(user.isPrivate.isFalse())
                        .and(post.writer.id.ne(userId))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(laon1).on(post.writer.id.eq(laon1.laon.id))
                                        .where(laon1.user.id.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Post> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable) {
        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .join(post.writer, user)
                .fetchJoin()
                .where(post.center.id.eq(centerId)
                        .and(post.isDeleted.isFalse())
                        .and(user.isPrivate.isFalse()
                                .or(post.writer.id.eq(userId)))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Post> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable) {
        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .join(post.writer, user)
                .fetchJoin()
                .where(post.center.id.eq(centerId)
                        .and(post.id.in(
                                JPAExpressions
                                        .select(climbingHistory.post.id)
                                        .from(climbingHistory)
                                        .where(climbingHistory.holdInfo.id.eq(holdId))
                        ))
                        .and(post.isDeleted.isFalse())
                        .and(user.isPrivate.isFalse()
                                .or(post.writer.id.eq(userId)))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Post> findByNicknameAndCenterAndYearMonth(String userId, String nickname, String centerId, Integer year, Integer month, Pageable pageable) {
        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .join(post.writer, user)
                .fetchJoin()
                .join(post.center, center)
                .fetchJoin()
                .where(post.center.id.eq(centerId)
                        .and(post.isDeleted.isFalse())
                        .and(user.nickname.eq(nickname))
                        .and(user.isPrivate.isFalse()
                                .or(post.writer.id.eq(userId)))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId))))
                        .and(post.createdAt.between(from, to)));

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Integer countByCenterExceptBlockUser(String centerId, String userId) {
        return (int) jpaQueryFactory
                .selectFrom(post)
                .join(user).on(post.writer.id.eq(user.id))
                .where(post.center.id.eq(centerId)
                        .and(post.isDeleted.isFalse())
                        .and(user.isPrivate.isFalse()
                                .or(post.writer.id.eq(userId)))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ))
                .fetchCount();
    }

    public List<Post> findByCenterIdAndUserId(String centerId, String userId) {
        JPAQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .where(post.center.id.eq(centerId)
                        .and(post.writer.id.eq(userId))
                        .and(post.isDeleted.isFalse()))
                .orderBy(post.createdAt.desc());

        return query.fetch();
    }

    public Page<Center> findCenterByUser(String userId, Pageable pageable) {
        JPQLQuery<Center> query = jpaQueryFactory
                .select(post.center).from(post)
                .join(post.writer, user)
                .join(post.center, center)
                .where(post.isDeleted.isFalse()
                        .and(post.writer.id.eq(userId)));

        long totalCount = query.fetchCount();
        List<Center> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
