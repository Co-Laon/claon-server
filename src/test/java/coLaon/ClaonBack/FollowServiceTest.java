package coLaon.ClaonBack;


import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.user.domain.Follow;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.FollowResponseDto;
import coLaon.ClaonBack.user.repository.FollowRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.FollowService;
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
public class FollowServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    FollowRepository followRepository;

    @InjectMocks
    FollowService followService;

    private User follower;
    private User following;
    private Follow follow;

    @BeforeEach
    void setUp() {
        this.follower = User.of(
                "followerId",
                "test@gmail.com",
                "test",
                "경기도",
                "성남시",
                "",
                "123456",
                "test"
        );

        this.following = User.of(
                "followingId",
                "test@gmail.com",
                "test",
                "경기도",
                "성남시",
                "",
                "123456",
                "test"
        );

        this.follow = Follow.of(
                this.follower,
                this.following
        );
    }

    @Test
    @DisplayName("Success case for follow")
    void successFollow() {
        try (MockedStatic<Follow> mockedFollow = mockStatic(Follow.class)) {
            //given

            given(this.userRepository.findById("followerId")).willReturn(Optional.of(follower));
            given(this.userRepository.findById("followingId")).willReturn(Optional.of(following));
            given(Follow.of(this.follower, this.following)).willReturn(this.follow);

            given(this.followRepository.save(this.follow)).willReturn(this.follow);
            //when
            FollowResponseDto followResponseDto = this.followService.follow("followerId", "followingId");
            //then
            assertThat(followResponseDto).isNotNull();
            assertThat(followResponseDto.getFollowerId()).isEqualTo("followerId");
            assertThat(followResponseDto.getFollowingId()).isEqualTo("followingId");
        }
    }

    @Test
    @DisplayName("Success case for unfollow")
    void successUnFollow() {
        try (MockedStatic<Follow> mockedFollow = mockStatic(Follow.class)) {
            //given

            given(this.userRepository.findById("followerId")).willReturn(Optional.of(follower));
            given(this.userRepository.findById("followingId")).willReturn(Optional.of(following));
            given(this.followRepository.findByFollowerAndFollowing(this.follower, this.following)).willReturn(Optional.of(this.follow));

            //when
            FollowResponseDto followResponseDto = this.followService.unfollow("followerId", "followingId");
            //then
            assertThat(followResponseDto).isNotNull();
            assertThat(followResponseDto.getFollowerId()).isEqualTo("followerId");
            assertThat(followResponseDto.getFollowingId()).isEqualTo("followingId");
        }
    }





}

