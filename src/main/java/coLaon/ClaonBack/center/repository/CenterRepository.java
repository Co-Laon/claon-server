package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, String> {
    @Query(value = "SELECT * FROM TB_CENTER " +
            "WHERE name like %:keyword% order by name limit 3", nativeQuery = true)
    List<Center> searchCenter(@Param("keyword") String keyword);
}
