package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {
    Optional<PostComment> findByIdAndIsDeletedFalse(String id);
    Page<PostComment> findByPostAndParentCommentIsNullAndIsDeletedFalse(Post post, Pageable pageable);
    List<PostComment> findTop3ByParentCommentAndIsDeletedFalseOrderByCreatedAt(PostComment parentComment);
    Page<PostComment> findAllByParentCommentAndIsDeletedFalse(PostComment postComment, Pageable pageable);
}
