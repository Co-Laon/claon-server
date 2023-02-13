package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.User;
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
import static coLaon.ClaonBack.user.domain.QUser.user;

@Repository
public class UserRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public UserRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(User.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<User> searchUser(String userId, String nickname, Pageable pageable) {
        JPQLQuery<User> query = jpaQueryFactory
                .selectFrom(user)
                .where(user.nickname.containsIgnoreCase(nickname)
                        .and(user.imagePath.isNotNull())
                        .and(user.id.ne(userId))
                        .and(user.id.notIn(
                                JPAExpressions
                                        .select(user.id)
                                        .from(user)
                                        .join(blockUser).on(user.id.eq(blockUser.blockedUser.id))
                                        .where(blockUser.user.id.eq(userId))))
                        .and(user.id.notIn(
                                JPAExpressions
                                        .select(user.id)
                                        .from(user)
                                        .join(blockUser).on(user.id.eq(blockUser.user.id))
                                        .where(blockUser.blockedUser.id.eq(userId)))
                        ));

        long totalCount = query.fetchCount();
        List<User> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
