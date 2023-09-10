package com.claon.post.repository;

import com.claon.post.domain.BlockUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockUserRepository extends JpaRepository<BlockUser, String> {
    @Query(value = """
            SELECT * 
            FROM TB_BLOCK_USER As b 
            WHERE b.user_id = :userId AND b.block_user_id= :blockId 
            OR b.user_id = :blockId AND b.block_user_id = :userId
            """, nativeQuery = true)
    List<BlockUser> findBlock(@Param("userId") String userId, @Param("blockId") String blockId);
}
