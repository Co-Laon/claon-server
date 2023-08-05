package claon.post.repository;

import claon.post.domain.PostComment;
import claon.post.dto.CommentFindResponseDto;
import claon.post.dto.QCommentFindResponseDto;
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

import static claon.post.domain.QPostComment.postComment;
import static claon.user.domain.QBlockUser.blockUser;
import static claon.user.domain.QUser.user;

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
            String userNickname,
            Pageable pageable
    ) {
        JPQLQuery<CommentFindResponseDto> query = jpaQueryFactory
                .select(new QCommentFindResponseDto(
                        postComment,
                        postComment.childComments.size(),
                        Expressions.as(Expressions.constant(userNickname), "userNickname")))
                .from(postComment)
                .join(postComment.writer, user)
                .fetchJoin()
                .where(postComment.post.id.eq(postId)
                        .and(postComment.isDeleted.isFalse())
                        .and(postComment.parentComment.isNull())
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<CommentFindResponseDto> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<PostComment> findChildCommentByParentComment(String postCommentId, String userId, Pageable pageable) {
        JPQLQuery<PostComment> query = jpaQueryFactory
                .selectFrom(postComment)
                .join(postComment.writer, user)
                .fetchJoin()
                .where(postComment.parentComment.id.eq(postCommentId)
                        .and(postComment.isDeleted.isFalse())
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writer.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(postComment.id.notIn(
                                JPAExpressions
                                        .select(postComment.id)
                                        .from(postComment)
                                        .join(blockUser).on(postComment.writer.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<PostComment> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
