package coLaon.ClaonBack.laon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LaonLikeRepositoryCustomImpl implements LaonLikeRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}

