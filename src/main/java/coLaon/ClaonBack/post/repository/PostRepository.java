package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Optional<Post> findByIdAndIsDeletedFalse(String id);
    @Query(value = "SELECT p.id FROM TB_POST AS p WHERE p.user_id = :userId", nativeQuery = true)
    List<String> selectPostIdsByUserId(@Param("userId") String userId);
    @Query(value = "SELECT COUNT(p) " +
            "FROM ( " +
            "SELECT p " +
            "FROM TB_POST AS p " +
            "JOIN TB_USER AS u " +
            "ON p.user_id = u.id " +
            "WHERE p.center_id = :centerId AND p.is_deleted = false " +
            "AND (u.is_private = false OR p.user_id = :userId) " +
            "EXCEPT " +
            "SELECT p " +
            "FROM TB_POST AS p " +
            "RIGHT JOIN TB_BLOCK_USER AS bu " +
            "ON p.user_id = bu.block_user_id " +
            "WHERE bu.user_id = :userId " +
            "EXCEPT " +
            "SELECT p " +
            "FROM TB_POST AS p " +
            "RIGHT JOIN TB_BLOCK_USER AS bu " +
            "ON p.user_id = bu.user_id " +
            "WHERE bu.block_user_id = :userId) a" , nativeQuery = true)
    Integer selectCountByCenter(@Param("centerId")String centerId, @Param("userId")String userId);
}
