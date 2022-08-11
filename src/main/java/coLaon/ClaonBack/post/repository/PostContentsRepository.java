package coLaon.ClaonBack.post.repository;

import coLaon.ClaonBack.post.domain.PostContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostContentsRepository extends JpaRepository<PostContents, String> {
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from tb_post_contents as p where p.post_id = :postId", nativeQuery = true)
    void deleteAllByPost(@Param("postId") String postId);
}
