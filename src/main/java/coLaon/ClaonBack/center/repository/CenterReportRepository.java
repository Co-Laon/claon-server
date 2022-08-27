package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.CenterReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterReportRepository extends JpaRepository<CenterReport, String> {
}
