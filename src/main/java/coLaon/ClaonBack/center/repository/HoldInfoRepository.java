package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.HoldInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldInfoRepository extends JpaRepository<HoldInfo, String> {
    List<HoldInfo> findAllByCenter(Center center);
}
