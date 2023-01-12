package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.ClimbingHistory;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static coLaon.ClaonBack.post.domain.QClimbingHistory.climbingHistory;
import static coLaon.ClaonBack.post.domain.QPost.post;
import static coLaon.ClaonBack.user.domain.QUser.user;
import static coLaon.ClaonBack.user.domain.QBlockUser.blockUser;

@Repository
public class ClimbingHistoryRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public ClimbingHistoryRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(ClimbingHistory.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<ClimbingHistory> findHistoryByDate(String userId, Integer year, Integer month) {
        JPAQuery<ClimbingHistory> query = jpaQueryFactory
                .selectFrom(climbingHistory)
                .where(climbingHistory.post.id.in(
                        JPAExpressions
                                .select(post.id)
                                .from(post)
                                .join(user).on(post.writer.id.eq(user.id))
                                .where(user.isPrivate.isFalse()
                                        .and(post.isDeleted.isFalse())
                                        .and(post.createdAt.year().eq(year))
                                        .and(post.createdAt.month().eq(month)))
                ).and(climbingHistory.post.id.notIn(
                        JPAExpressions
                                .select(post.id)
                                .from(post)
                                .join(blockUser).on(post.writer.id.eq(blockUser.blockedUser.id))
                                .where(blockUser.user.id.eq(userId))
                )).and(climbingHistory.post.id.notIn(
                        JPAExpressions
                                .select(post.id)
                                .from(post)
                                .join(blockUser).on(post.writer.id.eq(blockUser.user.id))
                                .where(blockUser.blockedUser.id.eq(userId))
                )));

        return query.fetch();
    }
}
