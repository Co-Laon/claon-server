package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {
}
