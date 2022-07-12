package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {
    List<PostComment> findByPostAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAt(Post post);
    List<PostComment> findTop3ByParentCommentAndIsDeletedFalseOrderByCreatedAt(PostComment parentComment);
    List<PostComment> findAllByParentCommentAndIsDeletedFalseOrderByCreatedAt(PostComment postComment);
    Long countAllByParentCommentId(String postCommentId);
}
