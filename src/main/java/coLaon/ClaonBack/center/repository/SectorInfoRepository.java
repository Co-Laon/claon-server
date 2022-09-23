package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.SectorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorInfoRepository extends JpaRepository<SectorInfo, String> {
    List<SectorInfo> findAllByCenter(Center center);
}
