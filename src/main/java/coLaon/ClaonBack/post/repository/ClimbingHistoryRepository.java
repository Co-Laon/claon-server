package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.ClimbingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClimbingHistoryRepository extends JpaRepository<ClimbingHistory, String> {
    @Query("SELECT h FROM ClimbingHistory as h JOIN FETCH h.holdInfo JOIN FETCH h.post as p JOIN FETCH p.center WHERE p.id IN :postIds")
    List<ClimbingHistory> findByPostIds(@Param("postIds") List<String> postIds);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from tb_climbing_history as h where h.post_id = :postId", nativeQuery = true)
    void deleteAllByPost(@Param("postId") String postId);
}
