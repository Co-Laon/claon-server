package com.claon.post.repository;

import com.claon.post.config.QueryDslTestConfig;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostComment;
import com.claon.post.dto.CommentFindResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

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
    private final String CENTER_ID = "CENTER_ID";
    private Post post;
    private PostComment deletedComment;

    @BeforeEach
    void setUp() {
        this.post = postRepository.save(Post.of(
                CENTER_ID,
                "testContent1",
                USER_ID,
                List.of(),
                List.of()
        ));

        this.deletedComment = PostComment.of(
                "testContent1",
                USER_ID,
                post,
                null
        );
        this.deletedComment.delete();
        this.postCommentRepository.save(this.deletedComment);
    }

    @Test
    public void successFindByIdAndIsDeletedFalse() {
        // given
        String commentId = deletedComment.getId();

        // when
        Optional<PostComment> postCommentOptional = postCommentRepository.findByIdAndIsDeletedFalse(commentId);

        // then
        assertThat(postCommentOptional).isNotPresent();
    }

    @Test
    public void successFindParentCommentByPost() {
        // given
        String postId = post.getId();

        this.postCommentRepository.save(PostComment.of(
                "testContent1",
                USER_ID,
                post,
                null
        ));
//        this.postCommentRepository.save(PostComment.of(
//                "testContent2",
//                blockUser,
//                post,
//                null
//        ));

        // when
        Page<CommentFindResponseDto> commentList = postCommentRepositorySupport.findParentCommentByPost(postId, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(commentList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindChildCommentByParentComment() {
        // given
        PostComment parentComment = PostComment.of(
                "testContent1",
                USER_ID,
                post,
                null
        );
        this.postCommentRepository.save(parentComment);

        this.postCommentRepository.save(PostComment.of(
                "testChildContent1",
                USER_ID,
                post,
                parentComment
        ));
//        this.postCommentRepository.save(PostComment.of(
//                "testChildContent2",
//                blockUser,
//                post,
//                parentComment
//        ));

        String parentCommentId = parentComment.getId();

        // when
        Page<PostComment> commentList = postCommentRepositorySupport.findChildCommentByParentComment(parentCommentId, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(commentList.getContent().size()).isEqualTo(1);
    }
}
