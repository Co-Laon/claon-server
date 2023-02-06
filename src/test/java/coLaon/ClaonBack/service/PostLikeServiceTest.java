package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepositorySupport;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.service.PostLikeService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostLikeServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    PostLikeRepositorySupport postLikeRepositorySupport;
    @Mock
    BlockUserRepository blockUserRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostLikeService postLikeService;

    private PostLike postLike, postLike2;
    private User user, blockedUser, privateUser;
    private BlockUser blockUser;
    private Post post, post2, blockedPost, privatePost;

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

        User user2 = User.of(
                "test123@gmail.com",
                "test2345!!",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(user2, "id", "testUserId2");

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

        Center center = Center.of(
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
        ReflectionTestUtils.setField(center, "id", "center1");

        this.post = Post.of(
                center,
                "testContent1",
                user,
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");
        ReflectionTestUtils.setField(this.post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post, "updatedAt", LocalDateTime.now());

        this.postLike = PostLike.of(
                user,
                post
        );
        ReflectionTestUtils.setField(this.postLike, "id", "testPostLikeId");

        this.postLike2 = PostLike.of(
                user2,
                post
        );
        ReflectionTestUtils.setField(this.postLike2, "id", "testPostLikeId2");

        HoldInfo holdInfo1 = HoldInfo.of(
                "holdName1",
                "/hold1.png",
                center
        );
        ReflectionTestUtils.setField(holdInfo1, "id", "holdId1");

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
                List.of(),
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
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedPostLike = mockStatic(PostLike.class)) {
            // given
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.empty());

            mockedPostLike.when(() -> PostLike.of(user, post)).thenReturn(postLike);
            given(this.postLikeRepository.countByPost(post)).willReturn(1);

            given(this.postLikeRepository.save(this.postLike)).willReturn(postLike);

            // when
            LikeResponseDto likeResponseDto = this.postLikeService.createLike(user, "testPostId");

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
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
        given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.of(postLike));

        // when
        LikeResponseDto likeResponseDto = this.postLikeService.deleteLike(user, "testPostId");

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
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike, postLike2), pageable, 2);

        given(this.postLikeRepositorySupport.findAllByPost(post.getId(), user.getId(), pageable)).willReturn(postLikes);

        // when
        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postLikeService.findLikeByPost(user, "testPostId", pageable);

        // then
        assertThat(likeFindResponseDto.getResults())
                .isNotNull()
                .extracting(LikeFindResponseDto::getPostId, LikeFindResponseDto::getLikerNickname)
                .contains(
                        tuple("testPostId", postLike.getLiker().getNickname()),
                        tuple("testPostId", postLike2.getLiker().getNickname())
                );
    }

    @Test
    @DisplayName("Success case for find likes of own post")
    void successFindOwnPostLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike, postLike2), pageable, 2);

        given(this.postLikeRepositorySupport.findAllByPost(privatePost.getId(), privateUser.getId(), pageable)).willReturn(postLikes);

        // when
        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postLikeService.findLikeByPost(privateUser, "privatePostId", pageable);

        // then
        assertThat(likeFindResponseDto.getResults())
                .isNotNull()
                .extracting(LikeFindResponseDto::getPostId, LikeFindResponseDto::getLikerNickname)
                .contains(
                        tuple("testPostId", postLike.getLiker().getNickname()),
                        tuple("testPostId", postLike2.getLiker().getNickname())
                );
    }

    @Test
    @DisplayName("Failure case for find likes of private post")
    void failFindLikesPrivatePost() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postLikeService.findLikeByPost(user, "privatePostId", pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
    }

    @Test
    @DisplayName("Failure case for find likes when blocked user")
    void failFindLikesBlockedUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postLikeService.findLikeByPost(user, "blockedPostId", pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
    }
}
