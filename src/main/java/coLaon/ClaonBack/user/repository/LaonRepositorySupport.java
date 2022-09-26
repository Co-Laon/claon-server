package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.center.dto.CenterPreviewResponseDto;
import coLaon.ClaonBack.user.domain.Laon;
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

import static coLaon.ClaonBack.user.domain.QBlockUser.blockUser;
import static coLaon.ClaonBack.user.domain.QLaon.laon1;

@Repository
public class LaonRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public LaonRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Laon.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<Laon> findAllByUserId(String userId, Pageable pageable) {
        JPQLQuery<Laon> query = jpaQueryFactory
                .selectFrom(laon1)
                .where(laon1.user.id.eq(userId)
                        .and(laon1.laon.id.notIn(
                                JPAExpressions
                                        .select(blockUser.user.id)
                                        .from(blockUser)
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<Laon> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
