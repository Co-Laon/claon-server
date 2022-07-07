package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.Follow;
import coLaon.ClaonBack.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}
