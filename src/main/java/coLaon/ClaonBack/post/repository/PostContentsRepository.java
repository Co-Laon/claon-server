package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.PostContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostContentsRepository extends JpaRepository<PostContents, String> {
}
