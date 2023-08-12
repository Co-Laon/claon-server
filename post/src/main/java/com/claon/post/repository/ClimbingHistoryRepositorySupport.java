package com.claon.post.repository;

import com.claon.post.domain.ClimbingHistory;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.claon.post.domain.QClimbingHistory.climbingHistory;
import static com.claon.post.domain.QPost.post;

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
                                .where(post.writerId.eq(userId)
                                        .and(post.isDeleted.isFalse())
                                        .and(post.createdAt.year().eq(year))
                                        .and(post.createdAt.month().eq(month)))
                ));

        return query.fetch();
    }
}
