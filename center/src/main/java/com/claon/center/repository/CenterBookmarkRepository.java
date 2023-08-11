package com.claon.center.repository;

import com.claon.center.domain.CenterBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CenterBookmarkRepository extends JpaRepository<CenterBookmark, String> {
    @Query(value = """
            SELECT * 
            FROM tb_center_bookmark AS b 
            WHERE b.user_id = :userId AND b.center_id = :centerId
            """, nativeQuery = true)
    Optional<CenterBookmark> findByUserIdAndCenterId(@Param("userId") String userId, @Param("centerId") String centerId);
}
