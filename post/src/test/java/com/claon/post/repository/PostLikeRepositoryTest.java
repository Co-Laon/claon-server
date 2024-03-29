package com.claon.post.repository;

import com.claon.post.config.QueryDslTestConfig;
import com.claon.post.domain.BlockUser;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QueryDslTestConfig.class, PostLikeRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PostLikeRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private PostLikeRepositorySupport postLikeRepositorySupport;
    @Autowired
    private BlockUserRepository blockUserRepository;

    private final String USER_ID = "USER_ID";
    private Post post;

    @BeforeEach
    void setUp() {
        this.post = postRepository.save(Post.of(
                "CENTER_ID",
                "testContent1",
                USER_ID,
                List.of(),
                List.of()
        ));

        postLikeRepository.save(PostLike.of(
                USER_ID, this.post
        ));
    }

    @Test
    public void successFindByLikerAndPost() {
        // when
        var postLike = postLikeRepository.findByLikerIdAndPost(USER_ID, this.post);

        // then
        assertThat(postLike).isPresent();
    }

    @Test
    public void successCountByPost() {
        // when
        var countPost = postLikeRepository.countByPost(this.post);

        // then
        assertThat(countPost).isEqualTo(1);
    }

    @Test
    public void successFindAllByPost() {
        // given
        String postId = this.post.getId();

        blockUserRepository.save(BlockUser.of(USER_ID, "BLOCKED_ID"));

        postLikeRepository.save(PostLike.of(
                "BLOCKED_ID", this.post
        ));

        // when
        var likerList = postLikeRepositorySupport.findAllByPost(postId, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(likerList.getContent().size()).isEqualTo(1);
    }
}
