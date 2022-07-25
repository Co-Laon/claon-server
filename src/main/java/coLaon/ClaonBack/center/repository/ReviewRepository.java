package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<CenterReview, String> {
    Optional<CenterReview> findById(String id);
    @Query(value = "SELECT cr.* " +
            "FROM TB_CENTER_REVIEW AS cr " +
            "WHERE cr.center_id = :centerId " +
            "EXCEPT " +
            "SELECT cr.* " +
            "FROM TB_CENTER_REVIEW AS cr " +
            "RIGHT JOIN TB_BLOCK_USER AS bu " +
            "ON cr.user_id = bu.block_user_id " +
            "WHERE bu.user_id = :userId " +
            "EXCEPT " +
            "SELECT cr.* " +
            "FROM TB_CENTER_REVIEW AS cr " +
            "RIGHT JOIN TB_BLOCK_USER AS bu " +
            "ON cr.user_id = bu.user_id " +
            "WHERE bu.block_user_id = :userId " +
            "ORDER BY created_at ASC", nativeQuery = true)
    Page<CenterReview> findByCenter(@Param("centerId")String centerId, @Param("userId")String userId, Pageable pageable);
    Integer countByCenter(Center center);
    @Query(value = "SELECT cr.rank " +
            "FROM TB_CENTER_REVIEW AS cr " +
            "WHERE cr.center_id = :centerId", nativeQuery = true)
    List<Integer> selectRanksByCenterId(@Param("centerId") String centerId);
    @Query(value = "SELECT * "
            + "FROM TB_CENTER_REVIEW AS r "
            + "WHERE r.user_id = :userId AND r.center_id = :centerId", nativeQuery = true)
    Optional<CenterReview> findByUserIdAndCenterId(@Param("userId") String userId, @Param("centerId") String centerId);
}

