package claon.user.repository;

import claon.user.domain.Laon;
import claon.user.dto.LaonFindResponseDto;
import claon.user.dto.QLaonFindResponseDto;
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

import static claon.user.domain.QBlockUser.blockUser;
import static claon.user.domain.QLaon.laon1;

@Repository
public class LaonRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public LaonRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Laon.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<LaonFindResponseDto> findAllByUserId(String userId, Pageable pageable) {
        JPQLQuery<LaonFindResponseDto> query = jpaQueryFactory
                .select(new QLaonFindResponseDto(laon1.laon.nickname, laon1.laon.imagePath))
                .from(laon1)
                .where(laon1.user.id.eq(userId)
                        .and(laon1.laon.id.notIn(
                                JPAExpressions
                                        .select(blockUser.user.id)
                                        .from(blockUser)
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        )
                );

        long totalCount = query.fetchCount();
        List<LaonFindResponseDto> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
