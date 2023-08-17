package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostComment;
import com.claon.post.domain.PostContents;
import com.claon.post.dto.*;
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
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostCommentService postCommentService;

    private final String USER_ID = "USER_ID";
    private final String CENTER_ID = "CENTER_ID";
    private Post post, blockedPost, privatePost;
    private PostComment postComment, privateComment;
    private PostComment childPostComment;

    @BeforeEach
    void setUp() {
        this.post = Post.of(
                CENTER_ID,
                "testContent",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                USER_ID
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");

        this.postComment = PostComment.of(
                "testContent",
                USER_ID,
                post,
                null
        );
        ReflectionTestUtils.setField(this.postComment, "id", "testCommentId");
        ReflectionTestUtils.setField(this.postComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.postComment, "updatedAt", LocalDateTime.now());

        this.childPostComment = PostComment.of(
                "testChildContent",
                USER_ID,
                post,
                postComment
        );
        ReflectionTestUtils.setField(this.childPostComment, "id", "testChildId");
        ReflectionTestUtils.setField(this.childPostComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.childPostComment, "updatedAt", LocalDateTime.now());

//        this.blockedPost = Post.of(
//                center,
//                "testContent3",
//                blockedUser,
//                List.of(),
//                List.of()
//        );
//        ReflectionTestUtils.setField(this.blockedPost, "id", "blockedPostId");
//        ReflectionTestUtils.setField(this.blockedPost, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(this.blockedPost, "updatedAt", LocalDateTime.now());
//
//        this.privatePost = Post.of(
//                center,
//                "testContent4",
//                privateUser,
//                List.of(),
//                List.of()
//        );
//        ReflectionTestUtils.setField(this.privatePost, "id", "privatePostId");
//        ReflectionTestUtils.setField(this.privatePost, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(this.privatePost, "updatedAt", LocalDateTime.now());
//
//        this.privateComment = PostComment.of(
//                "testContent1",
//                writer,
//                privatePost,
//                null
//        );
//        ReflectionTestUtils.setField(this.privateComment, "id", "privateCommentId");
//        ReflectionTestUtils.setField(this.privateComment, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(this.privateComment, "updatedAt", LocalDateTime.now());
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

            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

            mockedPostComment.when(() -> PostComment.of(
                    "testContent",
                    USER_ID,
                    this.post,
                    null
            )).thenReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment(USER_ID, "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content")
                    .contains(false, "testContent");
        }
    }

//    @Test
//    @DisplayName("Success case for create parent comment for own post")
//    void successCreateParentCommentForOwnPost() {
//        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
//            // given
//            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
//                    "testContent1",
//                    null
//            );
//
//            given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//            mockedPostComment.when(() -> PostComment.of(
//                    "testContent1",
//                    this.privateUser,
//                    this.privatePost,
//                    null
//            )).thenReturn(this.postComment);
//
//            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);
//
//            // when
//            CommentResponseDto commentResponseDto = this.postCommentService.createComment(privateUser, "privatePostId", commentRequestDto);
//
//            // then
//            assertThat(commentResponseDto)
//                    .isNotNull()
//                    .extracting("isDeleted", "content")
//                    .contains(false, "testContent1");
//        }
//    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testChildContent", postComment.getId());

            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

            mockedPostComment.when(() -> PostComment.of(
                    "testChildContent",
                    USER_ID,
                    this.post,
                    postComment
            )).thenReturn(this.childPostComment);

            given(this.postCommentRepository.save(this.childPostComment)).willReturn(this.childPostComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment(USER_ID, "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content", "parentCommentId")
                    .contains(false, "testChildContent", "testCommentId");
        }
    }

//    @Test
//    @DisplayName("Failure case for create parent comment for private user")
//    void failureCreateParentCommentForPrivateUser() {
//        // given
//        CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
//                "testContent",
//                null
//        );
//
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postCommentService.createComment(writer, "privatePostId", commentRequestDto)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
//    }

//    @Test
//    @DisplayName("Failure case for create parent comment for blocked user")
//    void failureCreateParentCommentForBlockedUser() {
//        // given
//        CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
//                "testContent1",
//                null
//        );
//
//        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
//        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postCommentService.createComment(writer, "blockedPostId", commentRequestDto)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
//    }

    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        Page<CommentFindResponseDto> parents = new PageImpl<>(
                List.of(new CommentFindResponseDto(postComment, 0, USER_ID)),
                pageable,
                2
        );

        given(this.postCommentRepositorySupport.findParentCommentByPost(post.getId(), USER_ID, pageable)).willReturn(parents);

        // when
        Pagination<CommentFindResponseDto> commentFindResponseDto = this.postCommentService.findCommentsByPost(USER_ID, "testPostId", pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(CommentFindResponseDto::getContent, CommentFindResponseDto::getCommentId)
                .containsExactly(
                        tuple("testContent", "testCommentId")
                );
    }

//    @Test
//    @DisplayName("Success case for find parent comments for own post")
//    void successFindParentCommentsForOwnPost() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        Page<CommentFindResponseDto> parents = new PageImpl<>(
//                List.of(
//                        new CommentFindResponseDto(postComment, 0, writer.getNickname()),
//                        new CommentFindResponseDto(postComment2, 0, writer.getNickname())
//                ),
//                pageable,
//                2
//        );
//
//        given(this.postCommentRepositorySupport.findParentCommentByPost(privatePost.getId(), privateUser.getId(), privateUser.getNickname(), pageable)).willReturn(parents);
//
//        // when
//        Pagination<CommentFindResponseDto> commentFindResponseDto = this.postCommentService.findCommentsByPost(privateUser, "privatePostId", pageable);
//
//        // then
//        assertThat(commentFindResponseDto.getResults())
//                .isNotNull()
//                .extracting(CommentFindResponseDto::getContent, CommentFindResponseDto::getCommentId)
//                .containsExactly(
//                        tuple("testContent1", "testCommentId"),
//                        tuple("testContent2", "testCommentId2")
//                );
//    }

//    @Test
//    @DisplayName("Failure case for find parent comment for private user")
//    void failureFindParentCommentForPrivateUser() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postCommentService.findCommentsByPost(writer, "privatePostId", pageable)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
//    }

//    @Test
//    @DisplayName("Failure case for find parent comment for blocked user")
//    void failureFindParentCommentForBlockedUser() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
//        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postCommentService.findCommentsByPost(writer, "blockedPostId", pageable)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
//    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<PostComment> children = new PageImpl<>(List.of(childPostComment), pageable, 2);

        given(this.postCommentRepository.findByIdAndIsDeletedFalse("testCommentId")).willReturn(Optional.of(postComment));
        given(this.postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), USER_ID, pageable)).willReturn(children);

        // when
        Pagination<ChildCommentResponseDto> commentFindResponseDto = this.postCommentService.findAllChildCommentsByParent(USER_ID, "testCommentId", pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(ChildCommentResponseDto::getContent, ChildCommentResponseDto::getIsDeleted)
                .containsExactly(
                        tuple("testChildContent", false)
                );
    }

//    @Test
//    @DisplayName("Success case for find child comments for own post")
//    void successFindChildCommentsForOwnPost() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        Page<PostComment> children = new PageImpl<>(List.of(childPostComment), pageable, 2);
//
//        given(this.postCommentRepository.findByIdAndIsDeletedFalse("privateCommentId")).willReturn(Optional.of(privateComment));
//        given(this.postCommentRepositorySupport.findChildCommentByParentComment(privateComment.getId(), privateUser.getId(), pageable)).willReturn(children);
//
//        // when
//        Pagination<ChildCommentResponseDto> commentFindResponseDto = this.postCommentService.findAllChildCommentsByParent(privateUser, "privateCommentId", pageable);
//
//        // then
//        assertThat(commentFindResponseDto.getResults())
//                .isNotNull()
//                .extracting(ChildCommentResponseDto::getContent, ChildCommentResponseDto::getIsDeleted)
//                .containsExactly(
//                        tuple("testChildContent1", false),
//                        tuple("testChildContent2", false)
//                );
//    }

    @Test
    @DisplayName("Success case for delete comment")
    void successDeleteComment() {
        // given
        given(this.postCommentRepository.findById("testChildId")).willReturn(Optional.of(childPostComment));

        given(this.postCommentRepository.save(childPostComment)).willReturn(childPostComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.deleteComment(USER_ID, "testChildId");

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "isDeleted")
                .contains("testChildId", true);
    }

    @Test
    @DisplayName("Failure case for delete comment because delete by other user")
    void failDeleteComment_Unauthorized() {
        // given
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.deleteComment("wrongId", "testCommentId")
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

        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        given(this.postCommentRepository.save(postComment)).willReturn(postComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.updateComment(USER_ID, "testCommentId", commentUpdateRequestDto);

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "content")
                .contains("testCommentId", "updateContent");
    }

    @Test
    @DisplayName("Failure case for update comment because update by other user")
    void failUpdateComment_Unauthorized() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.updateComment("wrongId", "testCommentId", commentUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}
