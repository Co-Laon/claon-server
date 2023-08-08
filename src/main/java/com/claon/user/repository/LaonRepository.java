package com.claon.user.repository;

import com.claon.user.domain.Laon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaonRepository extends JpaRepository<Laon, String> {
    @Query(value = """
            SELECT * 
            FROM TB_LAON AS l 
            WHERE l.laon_id = :laonId AND l.user_id = :userId
            """, nativeQuery = true)
    Optional<Laon> findByLaonIdAndUserId(@Param("laonId") String laonId, @Param("userId") String userId);

    @Query(value = """
            SELECT l.user_id 
            FROM TB_LAON AS l 
            WHERE l.laon_id = :userId
            """, nativeQuery = true)
    List<String> getUserIdsByLaonId(@Param("userId") String userId);
}
