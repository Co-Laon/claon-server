package coLaon.ClaonBack.laon.repository;

import coLaon.ClaonBack.laon.domain.LaonComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaonCommentRepository extends JpaRepository<LaonComment, String> {
}
