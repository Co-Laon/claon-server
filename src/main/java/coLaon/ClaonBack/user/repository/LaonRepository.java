package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.Laon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LaonRepository extends JpaRepository<Laon, String> {
    @Query(value = "SELECT * " +
            "FROM TB_LAON AS l " +
            "WHERE l.laon_id = :laonId AND l.user_id = :userId", nativeQuery = true)
    Optional<Laon> findByLaonIdAndUserId(@Param("laonId") String laonId, @Param("userId") String userId);

    @Query(value = "SELECT * " +
            "FROM TB_LAON AS l " +
            "WHERE l.user_id = :userId", nativeQuery = true)
    Page<Laon> findAllByUserId(@Param("userId") String userId, Pageable pageable);
    Long countByUserId(String userId);

    @Query(value = "SELECT l.laon_id " +
            "FROM TB_LAON AS l " +
            "l.user_id = :userId", nativeQuery = true)
    Set<String> getLaonIdsByUserId(@Param("userId") String userId);
}
