package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.Center;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, String> {
    @Query(value = "SELECT * FROM TB_CENTER " +
            "WHERE name like %:keyword% order by name limit 3", nativeQuery = true)
    List<Center> searchCenter(@Param("keyword") String keyword);

    @Query(value = "SELECT c FROM Center c WHERE c.createdAt > :time")
    Page<Center> findNewlyCreatedCenter(@Param("time") LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT c FROM Center as c INNER JOIN c.bookmarks as cb WHERE cb.user.id = :userId")
    Page<Center> findBookmarkedCenter(@Param("userId") String userId, Pageable pageable);

}
