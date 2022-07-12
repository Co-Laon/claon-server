package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaonRepository extends JpaRepository<Laon, String> {
    @Query(value = "SELECT * " +
            "FROM TB_LAON AS l " +
            "WHERE l.laon_id = :laonId AND l.user_id = :userId", nativeQuery = true)
    Optional<Laon> findByLaonIdAndUserId(String laonId, String userId);
}
