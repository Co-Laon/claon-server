package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.service.PostLikeService;
import coLaon.ClaonBack.user.domain.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostLikeServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostLikeService postLikeService;

    private PostLike postLike, postLike2;
    private User user, user2, privateUser;
    private Post post, post2;
    private PostContents postContents2;
    private HoldInfo holdInfo1;
    private Center center;
    private ClimbingHistory climbingHistory2;

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

        this.privateUser = User.of(
                "privateUserId",
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

        this.postContents2 = PostContents.of(
                "testPostContentsId2",
                post2,
                "test2.com/test.png"
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

        this.climbingHistory2 = ClimbingHistory.of(
                "climbingId2",
                this.post2,
                holdInfo1,
                0
        );

        this.post2 = Post.of(
                "testPostId2",
                center,
                "testContent2",
                user2,
                Set.of(postContents2),
                Set.of(climbingHistory2),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedPostLike = mockStatic(PostLike.class)) {
            // given
            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.empty());

            mockedPostLike.when(() -> PostLike.of(user, post)).thenReturn(postLike);
            given(this.postLikeRepository.countByPost(post)).willReturn(1);

            given(this.postLikeRepository.save(this.postLike)).willReturn(postLike);

            // when
            LikeResponseDto likeResponseDto = this.postLikeService.createLike("testUserId", "testPostId");

            // then
            assertThat(likeResponseDto)
                    .isNotNull()
                    .extracting("postId", "likeCount")
                    .contains("testPostId", 1);
        }
    }

    @Test
    @DisplayName("Success case for delete like")
    void successDeleteLike() {
        // given
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
        given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.of(postLike));

        // when
        LikeResponseDto likeResponseDto = this.postLikeService.deleteLike("testUserId", "testPostId");

        // then
        assertThat(likeResponseDto)
                .isNotNull()
                .extracting("postId", "likeCount")
                .contains("testPostId", 0);
    }

    @Test
    @DisplayName("Success case for find likes")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike, postLike2), pageable, 2);

        given(this.postLikeRepository.findAllByPost(post, pageable)).willReturn(postLikes);

        // when
        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postLikeService.findLikeByPost("testUserId", "testPostId", pageable);

        // then
        assertThat(likeFindResponseDto.getResults())
                .isNotNull()
                .extracting(LikeFindResponseDto::getPostId, LikeFindResponseDto::getLikerNickname)
                .contains(
                        tuple("testPostId", postLike.getLiker().getNickname()),
                        tuple("testPostId", postLike2.getLiker().getNickname())
                );
    }
}
