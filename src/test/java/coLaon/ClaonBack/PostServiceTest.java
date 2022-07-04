package coLaon.ClaonBack;

import coLaon.ClaonBack.post.Service.LaonService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.repository.LaonLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    LaonLikeRepository laonLikeRepository;

    @InjectMocks
    LaonService laonService;

    private PostLike postLike;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "testUserId",
                "01012341234",
                "test@gmail.com",
                "test1234!!",
                "test",
                "경기도",
                "성남시",
                "",
                "instagramId"
        );

        this.post = Post.of(
                "testLaonId",
                "center1",
                "wall",
                "hold",
                "testUrl",
                null,
                "test",
                user
        );

        this.postLike = PostLike.of(
                "testLaonLikeId",
                user,
                post
        );
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedLaonLike = mockStatic(PostLike.class)) {
            //given
            LikeRequestDto likeRequestDto = new LikeRequestDto("testLaonId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.postRepository.findById("testLaonId")).willReturn(Optional.of(post));

            given(PostLike.of(user, post)).willReturn(postLike);

            given(this.laonLikeRepository.save(this.postLike)).willReturn(postLike);
            //when
            LikeResponseDto likeResponseDto = this.laonService.createLike("testUserId", likeRequestDto);

            //then
            assertThat(likeResponseDto).isNotNull();
            assertThat(likeResponseDto.getId()).isEqualTo("testLaonLikeId");
        }
    }
}
