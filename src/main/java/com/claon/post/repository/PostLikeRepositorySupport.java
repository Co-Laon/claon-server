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

import static claon.post.domain.QPostLike.postLike;
import static claon.user.domain.QBlockUser.blockUser;
import static claon.user.domain.QUser.user;

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
                .join(postLike.liker, user)
                .fetchJoin()
                .where(postLike.post.id.eq(postId)
                        .and(postLike.liker.id.notIn(
                                JPAExpressions
                                        .select(blockUser.blockedUser.id)
                                        .from(blockUser)
                                        .where(blockUser.user.id.eq(userId))))
                        .and(postLike.liker.id.notIn(
                                JPAExpressions
                                        .select(blockUser.user.id)
                                        .from(blockUser)
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<PostLike> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
