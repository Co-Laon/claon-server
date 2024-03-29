package com.claon.post.repository;

import com.claon.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    @Query(value = "SELECT p FROM Post AS p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findByIdAndIsDeletedFalse(@Param("id") String id);
    @Query(value = "SELECT p.id FROM TB_POST AS p WHERE p.user_id = :userId AND p.is_deleted = false", nativeQuery = true)
    List<String> selectPostIdsByUserId(@Param("userId") String userId);
    @Query(value = """
            SELECT p FROM Post AS p
            WHERE p.writerId = :writerId AND p.isDeleted = false
            """,
            countQuery = "SELECT count(p) FROM Post p WHERE p.writerId = :writerId AND p.isDeleted = false")
    Page<Post> findByWriterAndIsDeletedFalse(@Param("writerId") String writerId, Pageable pageable);
}
