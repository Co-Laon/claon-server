package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
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
        this.post = Post.of(
                CENTER_ID,
                "testContent1",
                USER_ID,
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                List.of()
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");
        ReflectionTestUtils.setField(this.post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post, "updatedAt", LocalDateTime.now());

        this.climbingHistory = ClimbingHistory.of(
                this.post,
                HOLD_ID,
                0
        );
        ReflectionTestUtils.setField(this.climbingHistory, "id", "climbingId");

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

        this.postReport = PostReport.of(
                USER_ID,
                post,
                PostReportType.INAPPROPRIATE_POST,
                "testContent"
        );
        ReflectionTestUtils.setField(this.postReport, "id", "postReportId");
    }

    @Test
    @DisplayName("Success case for find user posts by center and year-month")
    void successFindUserPostsByCenterAndYearMonth() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);
        LocalDateTime now = LocalDateTime.now();

        given(this.postRepositorySupport.findByNicknameAndCenterAndYearMonth(USER_ID, "test", CENTER_ID, now.getYear(), now.getMonthValue(), pageable))
                .willReturn(postPage);

        // when
        Pagination<PostDetailResponseDto> posts = this.postService.findUserPostsByCenterAndYearMonth(USER_ID, "test", CENTER_ID, now.getYear(), now.getMonthValue(), pageable);

        // then
        assertThat(posts.getResults())
                .isNotNull()
                .extracting(PostDetailResponseDto::getPostId, PostDetailResponseDto::getContent)
                .contains(
                        tuple("testPostId", post.getContent())
                );
    }

    @Test
    @DisplayName("Success case for find posts")
    void successFindPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 2);

        given(this.postLikeRepository.countByPost(post)).willReturn(3);

        given(this.postRepositorySupport.findExceptLaonUserAndBlockUser(USER_ID, pageable)).willReturn(postPage);

        // when
        Pagination<PostDetailResponseDto> posts = this.postService.findPosts(USER_ID, pageable);

        //then
        assertThat(posts.getResults())
                .isNotNull()
                .extracting(PostDetailResponseDto::getPostId, PostDetailResponseDto::getContent)
                .contains(
                        tuple("testPostId", post.getContent())
                );
    }

    @Test
    @DisplayName("Success case for find post")
    void successFindPost() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
        given(this.postLikeRepository.countByPost(post)).willReturn(2);

        // when
        PostDetailResponseDto postResponseDto = this.postService.findPost(USER_ID, "testPostId");

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting(
                        post -> post.getContentsList().get(0),
                        PostDetailResponseDto::getCenterId)
                .contains(post.getContentList().get(0).getUrl(), CENTER_ID);
    }

//    @Test
//    @DisplayName("Success case for find own post")
//    void successFindOwnPost() {
//        // given
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//        given(this.postLikeRepository.countByPost(privatePost)).willReturn(2);
//
//        // when
//        PostDetailResponseDto postResponseDto = this.postService.findPost(privateUser, "privatePostId");
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
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postService.findPost(user, "privatePostId")
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
//        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
//        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postService.findPost(user, "blockedPostId")
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

            given(this.postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    HOLD_ID,
                    1
            )).thenReturn(climbingHistory);

            given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            PostResponseDto postResponseDto = this.postService.createPost(USER_ID, postCreateRequestDto);

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

            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    HOLD_ID,
                    1
            )).thenReturn(climbingHistory);

            given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            PostResponseDto postResponseDto = this.postService.updatePost(USER_ID, "testPostId", postUpdateRequestDto);

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

        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.updatePost("wrongId", "testPostId", postUpdateRequestDto)
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
        given(this.postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        given(this.postRepository.save(post)).willReturn(post);

        // when
        PostResponseDto postResponseDto = this.postService.deletePost(USER_ID, post.getId());

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting("postId", "isDeleted")
                .contains(post.getId(), true);
    }

    @Test
    @DisplayName("Failure case for post delete when request user is not writer")
    void failureDeletePost() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.deletePost("wrongId", post.getId())
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

            given(this.postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
            given(this.postReportRepository.findByReporterIdAndPost(USER_ID, post)).willReturn(Optional.empty());

            mockedPostReport.when(() -> PostReport.of(USER_ID, post, PostReportType.INAPPROPRIATE_POST, "testContent")).thenReturn(postReport);

            given(this.postReportRepository.save(this.postReport)).willReturn(postReport);

            // when
            PostReportResponseDto postReportResponseDto = this.postService.createReport(USER_ID, post.getId(), postReportRequestDto);

            // then
            assertThat(postReportResponseDto)
                    .isNotNull()
                    .extracting("postId", "reportType")
                    .contains(post.getId(), PostReportType.INAPPROPRIATE_POST);
        }
    }
}