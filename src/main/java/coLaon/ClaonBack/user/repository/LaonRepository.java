package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaonRepository extends JpaRepository<Laon, String> {
    Optional<Laon> findByLaonIdAndUserId(String laonId, String userId);
}
