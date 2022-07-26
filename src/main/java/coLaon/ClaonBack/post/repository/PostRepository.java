package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Optional<Post> findByIdAndIsDeletedFalse(String id);
}
