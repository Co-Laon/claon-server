package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Post> findByWriterOrderByCreatedAtDesc(User writer, Pageable pageable);
}
