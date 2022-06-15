package coLaon.ClaonBack.laon.repository;

import coLaon.ClaonBack.laon.domain.Laon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaonRepository extends JpaRepository<Laon, String> {
}
