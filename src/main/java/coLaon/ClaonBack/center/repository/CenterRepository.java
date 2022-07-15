package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends JpaRepository<Center, String> {
}
