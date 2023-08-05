package claon.version.repository;

import claon.version.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, String> {
    @Query(value = "SELECT * " +
            "FROM TB_APP_VERSION AS a " +
            "WHERE a.key = :key", nativeQuery = true)
    Optional<AppVersion> findByKey(@Param("key") String key);
}
