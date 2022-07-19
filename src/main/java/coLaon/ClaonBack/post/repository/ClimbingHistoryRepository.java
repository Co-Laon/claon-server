package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.ClimbingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimbingHistoryRepository extends JpaRepository<ClimbingHistory, String> {
}
