package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
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
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.service.PostService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.PostContentsDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.repository.PostContentsRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    UserRepository userRepository;
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

    private PostLike postLike;
    private PostLike postLike2;
    private User user;
    private User user2;
    private Post post;
    private PostContents postContents;
    private HoldInfo holdInfo1;
    private Center center;
    private ClimbingHistory climbingHistory;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "testUserId",
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.user2 = User.of(
                "testUserId2",
                "test123@gmail.com",
                "test2345!!",
                "test2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.center = Center.of(
                "center1",
                "testCenter",
                "testAddress",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge("자유 패키지", "330,000")),
                "charge img test",
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );

        this.post = Post.of(
                "testPostId",
                center,
                "testContent1",
                user,
                Set.of(),
                Set.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.postContents = PostContents.of(
                "testPostContentsId",
                post,
                "test.com/test.png"
        );

        this.postLike = PostLike.of(
                "testPostLikeId",
                user,
                post
        );

        this.postLike2 = PostLike.of(
                "testPostLikeId2",
                user2,
                post
        );

        this.holdInfo1 = HoldInfo.of(
                "holdId1",
                "holdName1",
                "/hold1.png",
                center
        );

        this.climbingHistory = ClimbingHistory.of(
                this.post,
                holdInfo1
        );
    }

    @Test
    @DisplayName("Success case for create post")
    void successCreatePost() {
        MockedStatic<Post> mockedPost = mockStatic(Post.class);
        MockedStatic<PostContents> mockedPostContents = mockStatic(PostContents.class);
        MockedStatic<ClimbingHistory> mockedClimbingHistory = mockStatic(ClimbingHistory.class);

        // given
        PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                "center1",
                List.of("holdId1"),
                "testContent",
                List.of(new PostContentsDto("test.com/test.png"))
        );

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.centerRepository.findById("center1")).willReturn(Optional.of(center));
        given(this.holdInfoRepository.findById("holdId1")).willReturn(Optional.of(holdInfo1));

        mockedPost.when(() ->
                        Post.of(
                                center,
                                postCreateRequestDto.getContent(),
                                user
                        ))
                .thenReturn(post);
        given(this.postRepository.save(post)).willReturn(post);

        mockedPostContents.when(() ->
                        PostContents.of(
                                post,
                                "test.com/test.png"
                        ))
                .thenReturn(postContents);
        given(this.postContentsRepository.save(postContents)).willReturn(postContents);

        mockedClimbingHistory.when(() ->
                        ClimbingHistory.of(
                                post,
                                holdInfo1
                        ))
                .thenReturn(climbingHistory);
        given(this.climbingHistoryRepository.save(climbingHistory)).willReturn(climbingHistory);

        // when
        PostResponseDto postResponseDto = this.postService.createPost("testUserId", postCreateRequestDto);

        // then
        assertThat(postResponseDto).isNotNull();
        assertThat(postCreateRequestDto.getCenterId()).isEqualTo(postResponseDto.getCenterId());
        assertThat(postCreateRequestDto.getContentsList().size()).isEqualTo(1);
        assertThat(postCreateRequestDto.getHoldIdList().size()).isEqualTo(1);

        mockedPost.close();
        mockedPostContents.close();
    }

    @Test
    @DisplayName("Failure case when invalid image format for create post")
    void failCreatePost_InvalidImageFormat() {
        // given
        List<PostContentsDto> postContentsDtoList = Stream.of("png", "jpg", "gif")
                .map(s -> "test.com/test." + s)
                .map(PostContentsDto::new)
                .collect(Collectors.toList());

        PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                "center1",
                List.of("holdId1", "holdId2"),
                "testContent",
                postContentsDtoList
        );

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.centerRepository.findById("center1")).willReturn(Optional.of(center));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> postService.createPost("testUserId", postCreateRequestDto)
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_FORMAT);
    }

    @Test
    @DisplayName("Success case for delete post")
    void successDeletePost() {
        // given
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        given(this.postRepository.save(post)).willReturn(post);

        // when
        PostResponseDto postResponseDto = this.postService.deletePost("testPostId", "testUserId");

        // then
        assertThat(postResponseDto).isNotNull();
        assertThat(postResponseDto.getIsDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("Failure case for post delete")
    void failureDeletePost() {
        // given
        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(user2));
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.postService.deletePost("testPostId", "testUserId2")
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_ACCESSIBLE);
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedPostLike = mockStatic(PostLike.class)) {
            // given
            LikeRequestDto likeRequestDto = new LikeRequestDto("testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));
            given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.empty());

            mockedPostLike.when(() -> PostLike.of(user, post)).thenReturn(postLike);
            given(this.postLikeRepository.countByPost(post)).willReturn(1);

            given(this.postLikeRepository.save(this.postLike)).willReturn(postLike);

            // when
            LikeResponseDto likeResponseDto = this.postService.createLike("testUserId", likeRequestDto);

            // then
            assertThat(likeResponseDto).isNotNull();
            assertThat(likeResponseDto.getLikeNumber()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Success case for delete like")
    void successDeleteLike() {
        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto("testPostId");

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));
        given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.of(postLike));

        // when
        LikeResponseDto likeResponseDto = this.postService.deleteLike("testUserId", likeRequestDto);

        // then
        assertThat(likeResponseDto).isNotNull();
        assertThat(likeRequestDto.getPostId()).isEqualTo(likeResponseDto.getPostId());
    }

    @Test
    @DisplayName("Success case for find likes")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike, postLike2), pageable, 2);

        given(this.postLikeRepository.findAllByPost(post, pageable)).willReturn(postLikes);

        // when
        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postService.findLikeByPost("testPostId", pageable);

        // then
        assertThat(likeFindResponseDto.getResults().size()).isEqualTo(postLikes.getContent().size());
    }
}