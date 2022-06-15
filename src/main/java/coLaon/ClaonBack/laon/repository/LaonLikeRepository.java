package coLaon.ClaonBack.laon.repository;

import coLaon.ClaonBack.laon.domain.LaonLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaonLikeRepository extends JpaRepository<LaonLike, String> {
}
