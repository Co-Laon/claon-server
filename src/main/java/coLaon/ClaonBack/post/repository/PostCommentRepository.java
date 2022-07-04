package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {
    List<PostComment> findByPostAndParentCommentIsNullOrderByCreatedAt(Post post);
    List<PostComment> findFirst3ByParentCommentIdOrderByCreatedAt(String postCommentId);
    List<PostComment> findAllByParentCommentOrderByCreatedAt(PostComment postComment);
}
