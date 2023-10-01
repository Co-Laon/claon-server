package com.claon.post.service;

import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.*;
import com.claon.post.domain.enums.PostReportType;
import com.claon.post.dto.*;
import com.claon.post.dto.request.*;
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
    @Mock
    BlockUserRepository blockUserRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostService postService;

    private final RequestUserInfo USER_INFO = new RequestUserInfo("USER_ID");
    private final String HOLD_ID = "HOLD_ID";
    private final String CENTER_ID = "CENTER_ID";
    private Post post, blockedPost;
    private ClimbingHistory climbingHistory;
    private PostReport postReport;
    private BlockUser blockUser;

    @BeforeEach
    void setUp() {
        post = Post.of(
                CENTER_ID,
                "testContent1",
                "USER_ID",
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

        blockUser = BlockUser.of(
                USER_INFO.id(),
                "BLOCKED_ID"
        );

        blockedPost = Post.of(
                CENTER_ID,
                "testContent3",
                "BLOCKED_ID",
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(blockedPost, "id", "blockedPostId");
        ReflectionTestUtils.setField(blockedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(blockedPost, "updatedAt", LocalDateTime.now());

        postReport = PostReport.of(
                "USER_ID",
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

        given(postRepositorySupport.findByCenterAndYearMonth(USER_INFO.id(), CENTER_ID, now.getYear(), now.getMonthValue(), pageable))
                .willReturn(postPage);

        // when
        var posts = postService.findUserPostsByCenterAndYearMonth(USER_INFO, CENTER_ID, now.getYear(), now.getMonthValue(), pageable);

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

        given(postRepositorySupport.findExceptBlockUser(USER_INFO.id(), pageable)).willReturn(postPage);

        // when
        var posts = postService.findPosts(USER_INFO, pageable);

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
        var postResponseDto = postService.findPost(USER_INFO, post.getId());

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting(post -> post.getContentsList().get(0), PostDetailResponseDto::getCenterId)
                .contains(
                        post.getContentList().get(0).getUrl(), CENTER_ID
                );
    }

    @Test
    @DisplayName("Failure case for find post when blocked user")
    void failFindPostBlockUser() {
        // given
        given(postRepository.findByIdAndIsDeletedFalse(blockedPost.getId())).willReturn(Optional.of(blockedPost));
        given(blockUserRepository.findBlock(USER_INFO.id(), blockUser.getBlockedUserId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postService.findPost(USER_INFO, blockedPost.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriterId()));
    }

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
                    postCreateRequestDto.content(),
                    List.of(postContents),
                    USER_INFO.id()
            )).thenReturn(post);

            given(postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    HOLD_ID,
                    1
            )).thenReturn(climbingHistory);

            given(climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            var postResponseDto = postService.createPost(USER_INFO, postCreateRequestDto);

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
            var postResponseDto = postService.updatePost(USER_INFO, post.getId(), postUpdateRequestDto);

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
                () -> postService.updatePost(new RequestUserInfo("WRONG_ID"), post.getId(), postUpdateRequestDto)
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
        var postResponseDto = postService.deletePost(USER_INFO, post.getId());

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
                () -> postService.deletePost(new RequestUserInfo("WRONG_ID"), post.getId())
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
            given(postReportRepository.findByReporterIdAndPost(USER_INFO.id(), post)).willReturn(Optional.empty());

            mockedPostReport.when(() -> PostReport.of(USER_INFO.id(), post, PostReportType.INAPPROPRIATE_POST, "testContent")).thenReturn(postReport);

            given(postReportRepository.save(postReport)).willReturn(postReport);

            // when
            var postReportResponseDto = postService.createReport(USER_INFO, post.getId(), postReportRequestDto);

            // then
            assertThat(postReportResponseDto)
                    .isNotNull()
                    .extracting("postId", "reportType")
                    .contains(post.getId(), PostReportType.INAPPROPRIATE_POST);
        }
    }
}