package com.claon.post.repository;

import com.claon.post.domain.Post;
import com.claon.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {
    Optional<PostLike> findByLikerIdAndPost(String likerId, Post post);
    Integer countByPost(Post post);
}
