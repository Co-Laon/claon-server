package com.claon.post.service;

import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import com.claon.post.domain.PostReport;
import com.claon.post.domain.enums.PostReportType;
import com.claon.post.dto.*;
import com.claon.post.repository.*;
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
public class PostServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    ClimbingHistoryRepository climbingHistoryRepository;
    @Mock
    PostRepositorySupport postRepositorySupport;
    @Mock
    PostReportRepository postReportRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostService postService;

    private final String USER_ID = "USER_ID";
    private final String HOLD_ID = "HOLD_ID";
    private final String CENTER_ID = "CENTER_ID";
    private Post post, blockedPost, privatePost;
    private ClimbingHistory climbingHistory;
    private PostReport postReport;

    @BeforeEach
    void setUp() {
        post = Post.of(
                CENTER_ID,
                "testContent1",
                USER_ID,
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                List.of()
        );
        ReflectionTestUtils.setField(post, "id", "testPostId");
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());

        climbingHistory = ClimbingHistory.of(
                post,
                HOLD_ID,
                0
        );
        ReflectionTestUtils.setField(climbingHistory, "id", "climbingId");

//        blockedPost = Post.of(
//                center,
//                "testContent3",
//                blockedUser,
//                List.of(),
//                List.of()
//        );
//        ReflectionTestUtils.setField(blockedPost, "id", "blockedPostId");
//        ReflectionTestUtils.setField(blockedPost, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(blockedPost, "updatedAt", LocalDateTime.now());
//
//        privatePost = Post.of(
//                center,
//                "testContent4",
//                privateUser,
//                List.of(),
//                List.of()
//        );
//        ReflectionTestUtils.setField(privatePost, "id", "privatePostId");
//        ReflectionTestUtils.setField(privatePost, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(privatePost, "updatedAt", LocalDateTime.now());

        postReport = PostReport.of(
                USER_ID,
                post,
                PostReportType.INAPPROPRIATE_POST,
                "testContent"
        );
        ReflectionTestUtils.setField(postReport, "id", "postReportId");
    }

    @Test
    @DisplayName("Success case for find user posts by center and year-month")
    void successFindUserPostsByCenterAndYearMonth() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);
        LocalDateTime now = LocalDateTime.now();

        given(postRepositorySupport.findByNicknameAndCenterAndYearMonth(USER_ID, "test", CENTER_ID, now.getYear(), now.getMonthValue(), pageable))
                .willReturn(postPage);

        // when
        var posts = postService.findUserPostsByCenterAndYearMonth(USER_ID, "test", CENTER_ID, now.getYear(), now.getMonthValue(), pageable);

        // then
        assertThat(posts.getResults())
                .isNotNull()
                .extracting(PostDetailResponseDto::getPostId, PostDetailResponseDto::getContent)
                .contains(
                        tuple(post.getId(), post.getContent())
                );
    }

    @Test
    @DisplayName("Success case for find posts")
    void successFindPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 2);

        given(postLikeRepository.countByPost(post)).willReturn(3);

        given(postRepositorySupport.findExceptLaonUserAndBlockUser(USER_ID, pageable)).willReturn(postPage);

        // when
        var posts = postService.findPosts(USER_ID, pageable);

        //then
        assertThat(posts.getResults())
                .isNotNull()
                .extracting(PostDetailResponseDto::getPostId, PostDetailResponseDto::getContent)
                .contains(
                        tuple(post.getId(), post.getContent())
                );
    }

    @Test
    @DisplayName("Success case for find post")
    void successFindPost() {
        // given
        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
        given(postLikeRepository.countByPost(post)).willReturn(2);

        // when
        var postResponseDto = postService.findPost(USER_ID, post.getId());

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting(post -> post.getContentsList().get(0), PostDetailResponseDto::getCenterId)
                .contains(
                        post.getContentList().get(0).getUrl(), CENTER_ID
                );
    }

//    @Test
//    @DisplayName("Success case for find own post")
//    void successFindOwnPost() {
//        // given
//        given(postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//        given(postLikeRepository.countByPost(privatePost)).willReturn(2);
//
//        // when
//        PostDetailResponseDto postResponseDto = postService.findPost(privateUser, "privatePostId");
//
//        // then
//        assertThat(postResponseDto)
//                .isNotNull()
//                .extracting(
//                        PostDetailResponseDto::getPostId,
//                        PostDetailResponseDto::getCenterName)
//                .contains("privatePostId", center.getName());
//    }

//    @Test
//    @DisplayName("Failure case for find post when private user")
//    void failFindPostPrivateUser() {
//        // given
//        given(postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> postService.findPost(user, "privatePostId")
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
//    }

//    @Test
//    @DisplayName("Failure case for find post when blocked user")
//    void failFindPostBlockUser() {
//        // given
//        given(postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
//        given(blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> postService.findPost(user, "blockedPostId")
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
//    }

    @Test
    @DisplayName("Success case for create post")
    void successCreatePost() {
        PostContents postContents = PostContents.of(
                "test.com/test.png"
        );

        try (
                MockedStatic<Post> mockedPost = mockStatic(Post.class);
                MockedStatic<ClimbingHistory> mockedClimbingHistory = mockStatic(ClimbingHistory.class)
        ) {
            // given
            PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                    CENTER_ID,
                    List.of(new ClimbingHistoryRequestDto(HOLD_ID, 1)),
                    "testContent",
                    List.of(new PostContentsDto("test.com/test.png"))
            );

            mockedPost.when(() -> Post.of(
                    CENTER_ID,
                    postCreateRequestDto.getContent(),
                    List.of(postContents),
                    USER_ID
            )).thenReturn(post);

            given(postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    HOLD_ID,
                    1
            )).thenReturn(climbingHistory);

            given(climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            var postResponseDto = postService.createPost(USER_ID, postCreateRequestDto);

            // then
            assertThat(postResponseDto)
                    .isNotNull()
                    .extracting(
                            PostResponseDto::getCenterId, post -> post.getContentsList().size()
                    )
                    .contains(CENTER_ID, 1);
        }
    }

    @Test
    @DisplayName("Success case for update post")
    void successUpdatePost() {
        try (
                MockedStatic<ClimbingHistory> mockedClimbingHistory = mockStatic(ClimbingHistory.class)
        ) {
            // given
            PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(
                    List.of(new ClimbingHistoryRequestDto(HOLD_ID, 1)),
                    "testContent",
                    List.of(new PostContentsDto("test.com/test.png"))
            );

            given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
            given(postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    HOLD_ID,
                    1
            )).thenReturn(climbingHistory);

            given(climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            var postResponseDto = postService.updatePost(USER_ID, post.getId(), postUpdateRequestDto);

            // then
            assertThat(postResponseDto)
                    .isNotNull()
                    .extracting(
                            PostResponseDto::getCenterId, post -> post.getContentsList().size()
                    )
                    .contains(CENTER_ID, 1);
        }
    }

    @Test
    @DisplayName("Failure case for update post when request user is not writer")
    void failureUpdatePost_Unauthorized() {
        // given
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(
                List.of(new ClimbingHistoryRequestDto(HOLD_ID, 1)),
                "testContent",
                List.of(new PostContentsDto("test.com/test.png"))
        );

        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postService.updatePost("wrongId", post.getId(), postUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for delete post")
    void successDeletePost() {
        // given
        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        given(postRepository.save(post)).willReturn(post);

        // when
        var postResponseDto = postService.deletePost(USER_ID, post.getId());

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting("postId", "isDeleted")
                .contains(post.getId(), post.getIsDeleted());
    }

    @Test
    @DisplayName("Failure case for post delete when request user is not writer")
    void failureDeletePost() {
        // given
        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postService.deletePost("wrongId", post.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for create post report")
    void successCreatePostReport() {
        try (MockedStatic<PostReport> mockedPostReport = mockStatic(PostReport.class)) {
            // given
            PostReportRequestDto postReportRequestDto = new PostReportRequestDto(
                    PostReportType.INAPPROPRIATE_POST,
                    "testContent"
            );

            given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
            given(postReportRepository.findByReporterIdAndPost(USER_ID, post)).willReturn(Optional.empty());

            mockedPostReport.when(() -> PostReport.of(USER_ID, post, PostReportType.INAPPROPRIATE_POST, "testContent")).thenReturn(postReport);

            given(postReportRepository.save(postReport)).willReturn(postReport);

            // when
            var postReportResponseDto = postService.createReport(USER_ID, post.getId(), postReportRequestDto);

            // then
            assertThat(postReportResponseDto)
                    .isNotNull()
                    .extracting("postId", "reportType")
                    .contains(post.getId(), PostReportType.INAPPROPRIATE_POST);
        }
    }
}