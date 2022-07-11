package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmailAndOAuthId(String email, String oAuthId);
    Optional<User> findByInstagramOAuthId(String oAuthId);
}
