package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static coLaon.ClaonBack.post.domain.QPost.post;
import static coLaon.ClaonBack.user.domain.QBlockUser.blockUser;
import static coLaon.ClaonBack.user.domain.QUser.user;

@Repository
public class PostRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Post.class);
        this.jpaQueryFactory = jpaQueryFactory;
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
}
