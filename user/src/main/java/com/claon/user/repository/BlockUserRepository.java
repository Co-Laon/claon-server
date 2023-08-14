package com.claon.user.repository;

import com.claon.user.domain.BlockUser;
import com.claon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockUserRepository extends JpaRepository<BlockUser, String> {
    @Query(value = """
            SELECT * 
            FROM TB_BLOCK_USER AS b 
            WHERE b.user_id = :userId AND b.block_user_id = :blockUserId
            """, nativeQuery = true)
    Optional<BlockUser> findByUserIdAndBlockId(@Param("userId") String userId, @Param("blockUserId") String blockUserId);

    @Query(value = """
            SELECT b 
            FROM BlockUser AS b JOIN FETCH b.blockedUser 
            WHERE b.user = :user
            """, countQuery = "SELECT count(b) FROM BlockUser b WHERE b.user = :user")
    Page<BlockUser> findByUser(@Param("user") User user, Pageable pageable);

    @Query(value = """
            SELECT * 
            FROM TB_BLOCK_USER As b 
            WHERE b.user_id = :userId AND b.block_user_id= :blockId 
            OR b.user_id = :blockId AND b.block_user_id = :userId
            """, nativeQuery = true)
    List<BlockUser> findBlock(@Param("userId") String userId, @Param("blockId") String blockId);
}
