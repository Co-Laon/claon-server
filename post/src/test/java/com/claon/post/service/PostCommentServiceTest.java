package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.BlockUser;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostComment;
import com.claon.post.domain.PostContents;
import com.claon.post.dto.*;
import com.claon.post.repository.BlockUserRepository;
import com.claon.post.repository.PostCommentRepository;
import com.claon.post.repository.PostCommentRepositorySupport;
import com.claon.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {
    @Mock
    PostCommentRepository postCommentRepository;
    @Mock
    PostCommentRepositorySupport postCommentRepositorySupport;
    @Mock
    PostRepository postRepository;
    @Mock
    BlockUserRepository blockUserRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostCommentService postCommentService;

    private final RequestUserInfo USER_INFO = new RequestUserInfo("USER_ID");
    private Post post, blockedPost;
    private PostComment postComment;
    private PostComment childPostComment;
    private BlockUser blockUser;

    @BeforeEach
    void setUp() {
        post = Post.of(
                "CENTER_ID",
                "testContent",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                USER_INFO.id()
        );
        ReflectionTestUtils.setField(post, "id", "testPostId");

        postComment = PostComment.of(
                "testContent",
                USER_INFO.id(),
                post,
                null
        );
        ReflectionTestUtils.setField(postComment, "id", "testCommentId");
        ReflectionTestUtils.setField(postComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(postComment, "updatedAt", LocalDateTime.now());

        childPostComment = PostComment.of(
                "testChildContent",
                USER_INFO.id(),
                post,
                postComment
        );
        ReflectionTestUtils.setField(childPostComment, "id", "testChildId");
        ReflectionTestUtils.setField(childPostComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(childPostComment, "updatedAt", LocalDateTime.now());

        blockUser = BlockUser.of(USER_INFO.id(), "BLOCKED_ID");

        blockedPost = Post.of(
                "centerId",
                "testContent3",
                "BLOCKED_ID",
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(blockedPost, "id", "blockedPostId");
        ReflectionTestUtils.setField(blockedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(blockedPost, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                    "testContent",
                    null
            );

            given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

            mockedPostComment.when(() -> PostComment.of(
                    "testContent",
                    USER_INFO.id(),
                    post,
                    null
            )).thenReturn(postComment);

            given(postCommentRepository.save(postComment)).willReturn(postComment);

            // when
            var commentResponseDto = postCommentService.createComment(USER_INFO, post.getId(), commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content")
                    .contains(postComment.getIsDeleted(), postComment.getContent());
        }
    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testChildContent", postComment.getId());

            given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
            given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.of(postComment));

            mockedPostComment.when(() -> PostComment.of(
                    "testChildContent",
                    USER_INFO.id(),
                    post,
                    postComment
            )).thenReturn(childPostComment);

            given(postCommentRepository.save(childPostComment)).willReturn(childPostComment);

            // when
            var commentResponseDto = postCommentService.createComment(USER_INFO, post.getId(), commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content", "parentCommentId")
                    .contains(childPostComment.getIsDeleted(), childPostComment.getContent(), childPostComment.getParentComment().getId());
        }
    }

    @Test
    @DisplayName("Failure case for create parent comment for blocked user")
    void failureCreateParentCommentForBlockedUser() {
        // given
        CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                "testContent1",
                null
        );

        given(postRepository.findByIdAndIsDeletedFalse(blockedPost.getId())).willReturn(Optional.of(blockedPost));
        given(blockUserRepository.findBlock(USER_INFO.id(), blockedPost.getWriterId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postCommentService.createComment(USER_INFO, blockedPost.getId(), commentRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriterId()));
    }

    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        Page<CommentFindResponseDto> parents = new PageImpl<>(
                List.of(new CommentFindResponseDto(postComment, 0, USER_INFO.id())),
                pageable,
                2
        );

        given(postCommentRepositorySupport.findParentCommentByPost(post.getId(), USER_INFO.id(), pageable)).willReturn(parents);

        // when
        Pagination<CommentFindResponseDto> commentFindResponseDto = postCommentService.findCommentsByPost(USER_INFO, post.getId(), pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(CommentFindResponseDto::getContent, CommentFindResponseDto::getCommentId)
                .containsExactly(
                        tuple(postComment.getContent(), postComment.getId())
                );
    }

    @Test
    @DisplayName("Failure case for find parent comment for blocked user")
    void failureFindParentCommentForBlockedUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(postRepository.findByIdAndIsDeletedFalse(blockedPost.getId())).willReturn(Optional.of(blockedPost));
        given(blockUserRepository.findBlock(USER_INFO.id(), blockUser.getBlockedUserId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postCommentService.findCommentsByPost(USER_INFO, "blockedPostId", pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriterId()));
    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<PostComment> children = new PageImpl<>(List.of(childPostComment), pageable, 2);

        given(postCommentRepository.findByIdAndIsDeletedFalse(postComment.getId())).willReturn(Optional.of(postComment));
        given(postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), USER_INFO.id(), pageable)).willReturn(children);

        // when
        var commentFindResponseDto = postCommentService.findAllChildCommentsByParent(USER_INFO, postComment.getId(), pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(ChildCommentResponseDto::getContent, ChildCommentResponseDto::getIsDeleted)
                .containsExactly(
                        tuple(childPostComment.getContent(), childPostComment.getIsDeleted())
                );
    }

    @Test
    @DisplayName("Success case for delete comment")
    void successDeleteComment() {
        // given
        given(postCommentRepository.findById(childPostComment.getId())).willReturn(Optional.of(childPostComment));

        given(postCommentRepository.save(childPostComment)).willReturn(childPostComment);

        // when
        var commentResponseDto = postCommentService.deleteComment(USER_INFO, childPostComment.getId());

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "isDeleted")
                .contains(childPostComment.getId(), childPostComment.getIsDeleted());
    }

    @Test
    @DisplayName("Failure case for delete comment because delete by other user")
    void failDeleteComment_Unauthorized() {
        // given
        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postCommentService.deleteComment(new RequestUserInfo("WRONG_ID"), postComment.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for update comment")
    void successUpdateComment() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.of(postComment));

        given(postCommentRepository.save(postComment)).willReturn(postComment);

        // when
        var commentResponseDto = postCommentService.updateComment(USER_INFO, postComment.getId(), commentUpdateRequestDto);

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "content")
                .contains(postComment.getId(), commentUpdateRequestDto.getContent());
    }

    @Test
    @DisplayName("Failure case for update comment because update by other user")
    void failUpdateComment_Unauthorized() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postCommentService.updateComment(new RequestUserInfo("WRONG_ID"), postComment.getId(), commentUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}
