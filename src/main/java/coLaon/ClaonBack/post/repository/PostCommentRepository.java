package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {
    @Query(value = "SELECT pc FROM PostComment AS pc JOIN FETCH pc.writer JOIN FETCH pc.post WHERE pc.id = :id AND pc.isDeleted = false")
    Optional<PostComment> findByIdAndIsDeletedFalse(@Param("id") String id);
}
