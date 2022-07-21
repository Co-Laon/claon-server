package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.BlockUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockUserRepository extends JpaRepository<BlockUser, String> {
    @Query(value = "SELECT * " +
            "FROM TB_BLOCK_USER AS b " +
            "WHERE b.block_user_id = :blockId AND b.user_id = :userId", nativeQuery = true)
    Optional<BlockUser> findByUserIdAndBlockId(String userId, String blockId);
}
