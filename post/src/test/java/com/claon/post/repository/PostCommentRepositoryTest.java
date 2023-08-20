package com.claon.post.repository;

import com.claon.post.config.QueryDslTestConfig;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostComment;
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

@Import({QueryDslTestConfig.class, PostCommentRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PostCommentRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private PostCommentRepositorySupport postCommentRepositorySupport;

    private final String USER_ID = "USER_ID";
    private Post post;
    private PostComment deletedComment;

    @BeforeEach
    void setUp() {
        post = postRepository.save(Post.of(
                "CENTER_ID",
                "testContent1",
                USER_ID,
                List.of(),
                List.of()
        ));

        deletedComment = PostComment.of(
                "testContent1",
                USER_ID,
                post,
                null
        );
        deletedComment.delete();
        postCommentRepository.save(deletedComment);
    }

    @Test
    public void successFindByIdAndIsDeletedFalse() {
        // given
        String commentId = deletedComment.getId();

        // when
        var postCommentOptional = postCommentRepository.findByIdAndIsDeletedFalse(commentId);

        // then
        assertThat(postCommentOptional).isNotPresent();
    }

    @Test
    public void successFindParentCommentByPost() {
        // given
        String postId = post.getId();

        postCommentRepository.save(PostComment.of(
                "testContent",
                USER_ID,
                post,
                null
        ));
//        postCommentRepository.save(PostComment.of(
//                "testContent",
//                blockUser,
//                post,
//                null
//        ));

        // when
        var commentList = postCommentRepositorySupport.findParentCommentByPost(postId, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(commentList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindChildCommentByParentComment() {
        // given
        PostComment parentComment = PostComment.of(
                "testContent",
                USER_ID,
                post,
                null
        );
        postCommentRepository.save(parentComment);

        postCommentRepository.save(PostComment.of(
                "testChildContent",
                USER_ID,
                post,
                parentComment
        ));
//        postCommentRepository.save(PostComment.of(
//                "testChildContent",
//                blockUser,
//                post,
//                parentComment
//        ));

        String parentCommentId = parentComment.getId();

        // when
        var commentList = postCommentRepositorySupport.findChildCommentByParentComment(parentCommentId, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(commentList.getContent().size()).isEqualTo(1);
    }
}
