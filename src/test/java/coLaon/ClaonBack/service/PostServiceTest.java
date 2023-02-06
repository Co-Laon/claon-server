package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.domain.PostReport;
import coLaon.ClaonBack.post.domain.enums.PostReportType;
import coLaon.ClaonBack.post.dto.ClimbingHistoryRequestDto;
import coLaon.ClaonBack.post.dto.PostContentsDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.dto.PostReportRequestDto;
import coLaon.ClaonBack.post.dto.PostReportResponseDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.dto.PostUpdateRequestDto;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostReportRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
import coLaon.ClaonBack.post.service.PostService;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
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

import static coLaon.ClaonBack.post.domain.enums.PostReportType.INAPPROPRIATE_POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    BlockUserRepository blockUserRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    CenterRepository centerRepository;
    @Mock
    HoldInfoRepository holdInfoRepository;
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

    private User user, user2, blockedUser, privateUser;
    private Post post, post2, blockedPost, privatePost;
    private HoldInfo holdInfo1;
    private Center center;
    private ClimbingHistory climbingHistory;
    private BlockUser blockUser;
    private PostReport postReport;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.user, "id", "testUserId");

        this.user2 = User.of(
                "test123@gmail.com",
                "test2345!!",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user2, "id", "testUserId2");

        this.blockedUser = User.of(
                "test123@gmail.com",
                "test2345!!",
                "blockUser",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.blockedUser, "id", "blockUserId");

        this.blockUser = BlockUser.of(
                user,
                blockedUser
        );

        privateUser = User.of(
                "test123@gmail.com",
                "test2345!!",
                "privateUser",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        privateUser.changePublicScope();
        ReflectionTestUtils.setField(privateUser, "id", "privateUserId");

        this.center = Center.of(
                "testCenter",
                "testAddress",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        );
        ReflectionTestUtils.setField(this.center, "id", "center1");

        this.post = Post.of(
                center,
                "testContent1",
                user,
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                List.of()
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");
        ReflectionTestUtils.setField(this.post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post, "updatedAt", LocalDateTime.now());

        this.holdInfo1 = HoldInfo.of(
                "holdName1",
                "/hold1.png",
                center
        );
        ReflectionTestUtils.setField(this.holdInfo1, "id", "holdId1");

        this.climbingHistory = ClimbingHistory.of(
                this.post,
                holdInfo1,
                0
        );
        ReflectionTestUtils.setField(this.climbingHistory, "id", "climbingId");

        ClimbingHistory climbingHistory2 = ClimbingHistory.of(
                this.post2,
                holdInfo1,
                0
        );
        ReflectionTestUtils.setField(climbingHistory2, "id", "climbingId2");

        this.post2 = Post.of(
                center,
                "testContent2",
                user2,
                List.of(PostContents.of(
                        "test2.com/test.png"
                )),
                List.of(climbingHistory2)
        );
        ReflectionTestUtils.setField(this.post2, "id", "testPostId2");
        ReflectionTestUtils.setField(this.post2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post2, "updatedAt", LocalDateTime.now());

        this.blockedPost = Post.of(
                center,
                "testContent3",
                blockedUser,
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(this.blockedPost, "id", "blockedPostId");
        ReflectionTestUtils.setField(this.blockedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.blockedPost, "updatedAt", LocalDateTime.now());

        this.privatePost = Post.of(
                center,
                "testContent4",
                privateUser,
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(this.privatePost, "id", "privatePostId");
        ReflectionTestUtils.setField(this.privatePost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.privatePost, "updatedAt", LocalDateTime.now());

        this.postReport = PostReport.of(
                user,
                post,
                INAPPROPRIATE_POST,
                "testContent"
        );
        ReflectionTestUtils.setField(this.postReport, "id", "postReportId");
    }

    @Test
    @DisplayName("Success case for find user posts by center and year-month")
    void successFindUserPostsByCenterAndYearMonth() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        Page<Post> postPage = new PageImpl<>(List.of(post2), pageable, 1);
        LocalDateTime now = LocalDateTime.now();

        given(this.postRepositorySupport.findByNicknameAndCenterAndYearMonth(user.getId(), "test2", center.getId(), now.getYear(), now.getMonthValue(), pageable))
                .willReturn(postPage);

        // when
        Pagination<PostDetailResponseDto> posts = this.postService.findUserPostsByCenterAndYearMonth(user, "test2", center.getId(), now.getYear(), now.getMonthValue(), pageable);

        // then
        assertThat(posts.getResults())
                .isNotNull()
                .extracting(PostDetailResponseDto::getPostId, PostDetailResponseDto::getContent)
                .contains(
                        tuple("testPostId2", post2.getContent())
                );
    }

    @Test
    @DisplayName("Success case for find posts")
    void successFindPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(List.of(post, post2), pageable, 2);

        given(this.postLikeRepository.countByPost(post2)).willReturn(2);
        given(this.postLikeRepository.countByPost(post)).willReturn(3);

        given(this.postRepositorySupport.findExceptLaonUserAndBlockUser(user.getId(), pageable)).willReturn(postPage);

        // when
        Pagination<PostDetailResponseDto> posts = this.postService.findPosts(user, pageable);

        //then
        assertThat(posts.getResults())
                .isNotNull()
                .extracting(PostDetailResponseDto::getPostId, PostDetailResponseDto::getContent)
                .contains(
                        tuple("testPostId", post.getContent()),
                        tuple("testPostId2", post2.getContent())
                );
    }

    @Test
    @DisplayName("Success case for find post")
    void successFindPost() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId2")).willReturn(Optional.of(post2));
        given(this.postLikeRepository.countByPost(post2)).willReturn(2);

        // when
        PostDetailResponseDto postResponseDto = this.postService.findPost(user2, "testPostId2");

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting(
                        post -> post.getContentsList().get(0),
                        PostDetailResponseDto::getCenterName)
                .contains(post2.getContentList().get(0).getUrl(), center.getName());
    }

    @Test
    @DisplayName("Success case for find own post")
    void successFindOwnPost() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
        given(this.postLikeRepository.countByPost(privatePost)).willReturn(2);

        // when
        PostDetailResponseDto postResponseDto = this.postService.findPost(privateUser, "privatePostId");

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting(
                        PostDetailResponseDto::getPostId,
                        PostDetailResponseDto::getCenterName)
                .contains("privatePostId", center.getName());
    }

    @Test
    @DisplayName("Failure case for find post when private user")
    void failFindPostPrivateUser() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.findPost(user, "privatePostId")
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
    }

    @Test
    @DisplayName("Failure case for find post when blocked user")
    void failFindPostBlockUser() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.findPost(user, "blockedPostId")
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
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
                    "center1",
                    List.of(new ClimbingHistoryRequestDto("holdId1", 1)),
                    "testContent",
                    List.of(new PostContentsDto("test.com/test.png"))
            );

            given(this.centerRepository.findById("center1")).willReturn(Optional.of(center));
            given(this.holdInfoRepository.findById("holdId1")).willReturn(Optional.of(holdInfo1));

            mockedPost.when(() -> Post.of(
                    center,
                    postCreateRequestDto.getContent(),
                    List.of(postContents),
                    user
            )).thenReturn(post);

            given(this.postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    holdInfo1,
                    1
            )).thenReturn(climbingHistory);

            given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            PostResponseDto postResponseDto = this.postService.createPost(user, postCreateRequestDto);

            // then
            assertThat(postResponseDto)
                    .isNotNull()
                    .extracting(
                            PostResponseDto::getCenterId,
                            post -> post.getContentsList().size())
                    .contains("center1", 1);
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
                    List.of(new ClimbingHistoryRequestDto("holdId1", 1)),
                    "testContent",
                    List.of(new PostContentsDto("test.com/test.png"))
            );

            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.holdInfoRepository.findById("holdId1")).willReturn(Optional.of(holdInfo1));
            given(this.postRepository.save(post)).willReturn(post);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    holdInfo1,
                    1
            )).thenReturn(climbingHistory);

            given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            PostResponseDto postResponseDto = this.postService.updatePost(user, "testPostId", postUpdateRequestDto);

            // then
            assertThat(postResponseDto)
                    .isNotNull()
                    .extracting(
                            PostResponseDto::getCenterId,
                            post -> post.getContentsList().size())
                    .contains("center1", 1);
        }
    }

    @Test
    @DisplayName("Failure case for update post when request user is not writer")
    void failureUpdatePost_Unauthorized() {
        // given
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(
                List.of(new ClimbingHistoryRequestDto("holdId1", 1)),
                "testContent",
                List.of(new PostContentsDto("test.com/test.png"))
        );

        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.updatePost(user2, "testPostId", postUpdateRequestDto)
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
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        given(this.postRepository.save(post)).willReturn(post);

        // when
        PostResponseDto postResponseDto = this.postService.deletePost(user, "testPostId");

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting("postId", "isDeleted")
                .contains("testPostId", true);
    }

    @Test
    @DisplayName("Failure case for post delete when request user is not writer")
    void failureDeletePost() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.deletePost(user2, "testPostId")
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

            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postReportRepository.findByReporterAndPost(user, post)).willReturn(Optional.empty());

            mockedPostReport.when(() -> PostReport.of(user, post, PostReportType.INAPPROPRIATE_POST, "testContent")).thenReturn(postReport);

            given(this.postReportRepository.save(this.postReport)).willReturn(postReport);

            // when
            PostReportResponseDto postReportResponseDto = this.postService.createReport(user, "testPostId", postReportRequestDto);

            // then
            assertThat(postReportResponseDto)
                    .isNotNull()
                    .extracting("postId", "reportType")
                    .contains("testPostId", PostReportType.INAPPROPRIATE_POST);
        }
    }
}