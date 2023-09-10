package com.claon.post.repository;

import com.claon.post.domain.PostLike;
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

import static com.claon.post.domain.QBlockUser.blockUser;
import static com.claon.post.domain.QPostLike.postLike;

@Repository
public class PostLikeRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public PostLikeRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(PostLike.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<PostLike> findAllByPost(String postId, String userId, Pageable pageable) {
        JPQLQuery<PostLike> query = jpaQueryFactory
                .selectFrom(postLike)
                .where(postLike.post.id.eq(postId)
                        .and(postLike.likerId.notIn(
                                JPAExpressions
                                        .select(blockUser.blockedUserId)
                                        .from(blockUser)
                                        .where(blockUser.userId.eq(userId))))
                        .and(postLike.likerId.notIn(
                                JPAExpressions
                                        .select(blockUser.userId)
                                        .from(blockUser)
                                        .where(blockUser.blockedUserId.eq(userId))))
                        );

        long totalCount = query.fetchCount();
        List<PostLike> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
