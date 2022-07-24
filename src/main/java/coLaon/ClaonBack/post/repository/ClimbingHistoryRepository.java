package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.ClimbingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClimbingHistoryRepository extends JpaRepository<ClimbingHistory, String> {

    @Query("SELECT h FROM ClimbingHistory as h LEFT JOIN FETCH h.post as p LEFT JOIN FETCH p.center WHERE p.id IN :postIds")
    List<ClimbingHistory> findByPostIds(List<String> postIds);
}
