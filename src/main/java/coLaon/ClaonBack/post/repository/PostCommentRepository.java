package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {
    Optional<PostComment> findByIdAndIsDeletedFalse(String id);
}
