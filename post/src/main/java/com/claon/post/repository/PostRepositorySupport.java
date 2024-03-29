package com.claon.post.repository;

import com.claon.post.domain.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.claon.post.domain.QBlockUser.blockUser;
import static com.claon.post.domain.QClimbingHistory.climbingHistory;
import static com.claon.post.domain.QPost.post;

@Repository
public class PostRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Post.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<Post> findExceptBlockUser(String userId, Pageable pageable) {
        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .where(post.isDeleted.isFalse()
                        .and(post.writerId.ne(userId))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                        );

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Post> findByCenterAndHoldExceptBlockUser(String centerId, Optional<String> holdId, String userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.centerId.eq(centerId));
        holdId.ifPresent(s -> builder.and(post.id.in(
                JPAExpressions
                        .select(climbingHistory.post.id)
                        .from(climbingHistory)
                        .where(climbingHistory.holdInfoId.eq(s))
        )));
        builder.and(post.isDeleted.isFalse());
        builder.and(post.writerId.eq(userId));
        builder.and(post.id.notIn(
                JPAExpressions
                        .select(post.id)
                        .from(post)
                        .join(blockUser).on(post.writerId.eq(blockUser.blockedUserId))
                        .where(blockUser.userId.eq(userId))));
        builder.and(post.id.notIn(
                JPAExpressions
                        .select(post.id)
                        .from(post)
                        .join(blockUser).on(post.writerId.eq(blockUser.userId))
                        .where(blockUser.blockedUserId.eq(userId))));

        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .where(builder);

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<Post> findByCenterAndYearMonth(String userId, String centerId, Integer year, Integer month, Pageable pageable) {
        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        JPQLQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .where(post.centerId.eq(centerId)
                        .and(post.isDeleted.isFalse())
                        .and(post.writerId.eq(userId))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                        .and(post.createdAt.between(from, to)));

        long totalCount = query.fetchCount();
        List<Post> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Long countByCenter(String userId, String centerId) {
        return jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.centerId.eq(centerId)
                        .and(post.isDeleted.isFalse())
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(post.id.notIn(
                                JPAExpressions
                                        .select(post.id)
                                        .from(post)
                                        .join(blockUser).on(post.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId)))))
                .fetchOne();
    }
}
