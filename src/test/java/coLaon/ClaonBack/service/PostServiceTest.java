package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.ClimbingHistoryRequestDto;
import coLaon.ClaonBack.post.dto.PostContentsDto;
import coLaon.ClaonBack.post.dto.PostUpdateRequestDto;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.service.PostService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.post.repository.PostContentsRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    BlockUserRepository blockUserRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    PostContentsRepository postContentsRepository;
    @Mock
    CenterRepository centerRepository;
    @Mock
    HoldInfoRepository holdInfoRepository;
    @Mock
    ClimbingHistoryRepository climbingHistoryRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostService postService;

    private User user, user2, blockedUser, privateUser;
    private Post post, post2, blockedPost, privatePost;
    private PostContents postContents, postContents2;
    private HoldInfo holdInfo1;
    private Center center;
    private ClimbingHistory climbingHistory;
    private BlockUser blockUser;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.user, "id", "testUserId");

        this.user2 = User.of(
                "test123@gmail.com",
                "test2345!!",
                "test2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user2, "id", "testUserId2");

        this.blockedUser = User.of(
                "test123@gmail.com",
                "test2345!!",
                "blockUser",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.blockedUser, "id", "blockUserId");

        this.blockUser = BlockUser.of(
                user,
                blockedUser
        );

        this.privateUser = User.of(
                "test123@gmail.com",
                "test2345!!",
                "privateUser",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );
        this.privateUser.changePublicScope();
        ReflectionTestUtils.setField(this.privateUser, "id", "privateUserId");

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
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );
        ReflectionTestUtils.setField(this.center, "id", "center1");

        this.post = Post.of(
                center,
                "testContent1",
                user,
                List.of(),
                Set.of()
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");
        ReflectionTestUtils.setField(this.post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post, "updatedAt", LocalDateTime.now());

        this.postContents = PostContents.of(
                post,
                "test.com/test.png"
        );
        ReflectionTestUtils.setField(this.postContents, "id", "testPostContentsId");

        this.postContents2 = PostContents.of(
                post2,
                "test2.com/test.png"
        );
        ReflectionTestUtils.setField(this.postContents2, "id", "testPostContentsId2");

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
                List.of(postContents2),
                Set.of(climbingHistory2)
        );
        ReflectionTestUtils.setField(this.post2, "id", "testPostId2");
        ReflectionTestUtils.setField(this.post2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post2, "updatedAt", LocalDateTime.now());

        this.blockedPost = Post.of(
                center,
                "testContent3",
                blockedUser,
                List.of(),
                Set.of()
        );
        ReflectionTestUtils.setField(this.blockedPost, "id", "blockedPostId");
        ReflectionTestUtils.setField(this.blockedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.blockedPost, "updatedAt", LocalDateTime.now());

        this.privatePost = Post.of(
                center,
                "testContent4",
                privateUser,
                List.of(),
                Set.of()
        );
        ReflectionTestUtils.setField(this.privatePost, "id", "privatePostId");
        ReflectionTestUtils.setField(this.privatePost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.privatePost, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for find post")
    void successFindPost() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId2")).willReturn(Optional.of(post2));
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postLikeRepository.countByPost(post2)).willReturn(2);

        // when
        PostDetailResponseDto postResponseDto = this.postService.findPost("testUserId", "testPostId2");

        // then
        assertThat(postResponseDto)
                .isNotNull()
                .extracting(
                        post -> post.getContentsList().get(0),
                        post -> post.getHoldList().get(0).getName(),
                        PostDetailResponseDto::getCenterName)
                .contains(postContents2.getUrl(), holdInfo1.getName(), center.getName());
    }

    @Test
    @DisplayName("Failure case for find post when private user")
    void failFindPostPrivateUser() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));

        // when
        final BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> this.postService.findPost("testUserId", "privatePostId")
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_ACCESSIBLE);
        assertThat(ex.getMessage()).isEqualTo("비공개 계정입니다.");
    }

    @Test
    @DisplayName("Failure case for find post when blocked user")
    void failFindPostBlockUser() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(Optional.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.findPost("testUserId", "blockedPostId")
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "조회가 불가능한 이용자입니다.");
    }

    @Test
    @DisplayName("Success case for create post")
    void successCreatePost() {
        try (
                MockedStatic<Post> mockedPost = mockStatic(Post.class);
                MockedStatic<PostContents> mockedPostContents = mockStatic(PostContents.class);
                MockedStatic<ClimbingHistory> mockedClimbingHistory = mockStatic(ClimbingHistory.class)
        ) {
            // given
            PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                    "center1",
                    List.of(new ClimbingHistoryRequestDto("holdId1", 1)),
                    "testContent",
                    List.of(new PostContentsDto("test.com/test.png"))
            );

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.centerRepository.findById("center1")).willReturn(Optional.of(center));
            given(this.holdInfoRepository.findById("holdId1")).willReturn(Optional.of(holdInfo1));

            mockedPost.when(() -> Post.of(
                    center,
                    postCreateRequestDto.getContent(),
                    user
            )).thenReturn(post);

            given(this.postRepository.save(post)).willReturn(post);

            mockedPostContents.when(() -> PostContents.of(
                    post,
                    "test.com/test.png"
            )).thenReturn(postContents);

            given(this.postContentsRepository.save(postContents)).willReturn(postContents);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    holdInfo1,
                    1
            )).thenReturn(climbingHistory);

            given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            PostResponseDto postResponseDto = this.postService.createPost("testUserId", postCreateRequestDto);

            // then
            assertThat(postResponseDto)
                    .isNotNull()
                    .extracting(
                            PostResponseDto::getCenterId,
                            post -> post.getContentsList().size(),
                            post -> post.getHoldList().size())
                    .contains("center1", 1, 1);
        }
    }

    @Test
    @DisplayName("Success case for update post")
    void successUpdatePost() {
        try (
                MockedStatic<PostContents> mockedPostContents = mockStatic(PostContents.class);
                MockedStatic<ClimbingHistory> mockedClimbingHistory = mockStatic(ClimbingHistory.class)
        ) {
            // given
            PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(
                    List.of(new ClimbingHistoryRequestDto("holdId1", 1)),
                    "testContent",
                    List.of(new PostContentsDto("test.com/test.png"))
            );

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.holdInfoRepository.findById("holdId1")).willReturn(Optional.of(holdInfo1));
            given(this.postRepository.save(post)).willReturn(post);

            mockedPostContents.when(() -> PostContents.of(
                    post,
                    "test.com/test.png"
            )).thenReturn(postContents);

            given(this.postContentsRepository.save(postContents)).willReturn(postContents);

            mockedClimbingHistory.when(() -> ClimbingHistory.of(
                    post,
                    holdInfo1,
                    1
            )).thenReturn(climbingHistory);

            given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

            // when
            PostResponseDto postResponseDto = this.postService.updatePost("testUserId", "testPostId", postUpdateRequestDto);

            // then
            assertThat(postResponseDto)
                    .isNotNull()
                    .extracting(
                            PostResponseDto::getCenterId,
                            post -> post.getContentsList().size(),
                            post -> post.getHoldList().size())
                    .contains("center1", 1, 1);
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

        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(user2));
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.updatePost("testUserId2", "testPostId", postUpdateRequestDto)
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
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        given(this.postRepository.save(post)).willReturn(post);

        // when
        PostResponseDto postResponseDto = this.postService.deletePost("testPostId", "testUserId");

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
        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(user2));
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postService.deletePost("testPostId", "testUserId2")
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for find posts by user nickname")
    void successFindPosts(){
        // given
        String loginedUserId = this.user.getId();

        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById(loginedUserId)).willReturn(Optional.of(this.user));
        given(this.userRepository.findByNickname(this.user2.getNickname())).willReturn(Optional.of(this.user2));
        given(this.blockUserRepository.findBlock(this.user2.getId(), loginedUserId)).willReturn(Optional.empty());
        given(this.postRepository.findByWriterOrderByCreatedAtDesc(this.user2, pageable)).willReturn(new PageImpl<>(List.of(this.post), pageable, 1));

        // when
        Pagination<PostThumbnailResponseDto> dtos = this.postService.getUserPosts(loginedUserId, this.user2.getNickname(), pageable);

        // then
        assertThat(dtos.getResults().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fail case(user is private) for find posts")
    void failFindPosts(){
        // given
        String loginedUserId = this.user.getId();
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById(loginedUserId)).willReturn(Optional.of(this.user));
        given(this.userRepository.findByNickname(this.privateUser.getNickname())).willReturn(Optional.of(this.privateUser));

        // when
        final BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> this.postService.getUserPosts(loginedUserId, this.privateUser.getNickname(), pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "비공개 계정입니다.");
    }
}