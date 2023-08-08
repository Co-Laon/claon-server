package com.claon.post.repository;

import com.claon.post.domain.Post;
import com.claon.user.domain.User;
import com.claon.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {
    Optional<PostLike> findByLikerAndPost(User liker, Post post);
    Integer countByPost(Post post);
    Long countByPostIdIn(List<String> postIds);
}
