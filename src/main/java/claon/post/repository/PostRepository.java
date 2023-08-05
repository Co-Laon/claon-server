package claon.post.repository;

import claon.post.domain.Post;
import claon.user.domain.User;
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
    @Query(value = "SELECT p FROM Post AS p JOIN FETCH p.center JOIN FETCH p.writer WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findByIdAndIsDeletedFalse(@Param("id") String id);
    @Query(value = "SELECT p.id FROM TB_POST AS p WHERE p.user_id = :userId AND p.is_deleted = false", nativeQuery = true)
    List<String> selectPostIdsByUserId(@Param("userId") String userId);
    @Query(value = "SELECT p FROM Post AS p JOIN FETCH p.center " +
            "WHERE p.writer = :writer AND p.isDeleted = false",
            countQuery = "SELECT count(p) FROM Post p WHERE p.writer = :writer AND p.isDeleted = false")
    Page<Post> findByWriterAndIsDeletedFalse(@Param("writer") User writer, Pageable pageable);
}
