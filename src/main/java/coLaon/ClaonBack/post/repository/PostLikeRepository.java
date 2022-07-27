package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {
    Optional<PostLike> findByLikerAndPost(User liker, Post post);
    Integer countByPost(Post post);
    Page<PostLike> findAllByPost(Post post, Pageable pageable);
    Long countByPostIdIn(List<String> postIds);
}
