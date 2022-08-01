package coLaon.ClaonBack.center.repository;

import coLaon.ClaonBack.center.domain.BookmarkCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkCenterRepository extends JpaRepository<BookmarkCenter, String> {
    @Query(value = "SELECT * " +
            "FROM TB_BOOKMARK_CENTER AS b " +
            "WHERE b.user_id = :userId AND b.center_id = :centerId", nativeQuery = true)
    Optional<BookmarkCenter> findByUserIdAndCenterId(@Param("userId") String userId, @Param("centerId") String centerId);
}
