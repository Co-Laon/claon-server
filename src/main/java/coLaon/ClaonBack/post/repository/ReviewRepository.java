package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.center.domain.CenterReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<CenterReview, String> {
}
