package com.claon.post.repository;

import com.claon.post.domain.PostComment;
import com.claon.post.dto.CommentFindResponseDto;
import com.claon.post.dto.QCommentFindResponseDto;
import com.querydsl.core.types.dsl.Expressions;
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
import static com.claon.post.domain.QPostComment.postComment;

@Repository
public class PostCommentRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public PostCommentRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(PostComment.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<CommentFindResponseDto> findParentCommentByPost(
            String postId,
            String userId,
            Pageable pageable
    ) {
        JPQLQuery<CommentFindResponseDto> query = jpaQueryFactory
                .select(new QCommentFindResponseDto(
                        postComment,
                        postComment.childComments.size(),
                        Expressions.as(Expressions.constant(userId), "userId")))
                .from(postComment)
                .where(postComment.post.id.eq(postId)
                        .and(postComment.isDeleted.isFalse())
                        .and(postComment.parentComment.isNull())
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                        );

        long totalCount = query.fetchCount();
        List<CommentFindResponseDto> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<PostComment> findChildCommentByParentComment(String postCommentId, String userId, Pageable pageable) {
        JPQLQuery<PostComment> query = jpaQueryFactory
                .selectFrom(postComment)
                .where(postComment.parentComment.id.eq(postCommentId)
                        .and(postComment.isDeleted.isFalse())
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writerId.eq(blockUser.blockedUserId))
                                        .where(blockUser.userId.eq(userId))))
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writerId.eq(blockUser.userId))
                                        .where(blockUser.blockedUserId.eq(userId))))
                        );

        long totalCount = query.fetchCount();
        List<PostComment> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
