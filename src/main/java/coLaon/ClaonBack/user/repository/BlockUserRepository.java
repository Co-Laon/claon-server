package coLaon.ClaonBack.user.repository;

import coLaon.ClaonBack.user.domain.BlockUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockUserRepository extends JpaRepository<BlockUser, String> {
    @Query(value = "SELECT * " +
            "FROM TB_BLOCK_USER AS b " +
            "WHERE b.user_id = :userId AND b.block_user_id = :blockId", nativeQuery = true)
    Optional<BlockUser> findByUserIdAndBlockId(@Param("userId") String userId, @Param("blockId") String blockId);

    @Query(value = "SELECT * " +
            "FROM TB_BLOCK_USER AS b " +
            "WHERE b.user_id = :userId", nativeQuery = true)
    Page<BlockUser> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM TB_BLOCK_USER As b " +
            "WHERE b.user_id = :userId AND b.block_user_id= :blockId " +
            "OR b.user_id = :blockId AND b.block_user_id = :userId", nativeQuery = true)
    Optional<BlockUser> findBlock(@Param("userId") String userId, @Param("blockId") String blockId);
}
