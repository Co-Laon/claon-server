package coLaon.ClaonBack;

import coLaon.ClaonBack.laon.Service.LaonService;
import coLaon.ClaonBack.laon.domain.Laon;
import coLaon.ClaonBack.laon.domain.LaonLike;
import coLaon.ClaonBack.laon.dto.LaonCreateRequestDto;
import coLaon.ClaonBack.laon.dto.LaonResponseDto;
import coLaon.ClaonBack.laon.dto.LikeRequestDto;
import coLaon.ClaonBack.laon.dto.LikeResponseDto;
import coLaon.ClaonBack.laon.repository.LaonLikeRepository;
import coLaon.ClaonBack.laon.repository.LaonRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class LaonServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LaonRepository laonRepository;
    @Mock
    LaonLikeRepository laonLikeRepository;

    @InjectMocks
    LaonService laonService;

    private LaonLike laonLike;
    private User user;
    private Laon laon;

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

        this.laon = Laon.of(
                "testLaonId",
                "center1",
                "wall",
                "hold",
                "testUrl",
                null,
                "test",
                user
        );

        this.laonLike = LaonLike.of(
                "testLaonLikeId",
                user,
                laon
        );
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<LaonLike> mockedLaonLike = mockStatic(LaonLike.class)) {
            //given
            LikeRequestDto likeRequestDto = new LikeRequestDto("testLaonId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.laonRepository.findById("testLaonId")).willReturn(Optional.of(laon));

            given(LaonLike.of(user, laon)).willReturn(laonLike);

            given(this.laonLikeRepository.save(this.laonLike)).willReturn(laonLike);
            //when
            LikeResponseDto likeResponseDto = this.laonService.createLike("testUserId", likeRequestDto);

            //then
            assertThat(likeResponseDto).isNotNull();
            assertThat(likeResponseDto.getId()).isEqualTo("testLaonLikeId");
        }
    }

    @Test
    @DisplayName("Success cass for create Laon")
    void successCreateLaon() {
        try (MockedStatic<Laon> mockedLaon = mockStatic(Laon.class)) {
            // given
            LaonCreateRequestDto laonCreateRequestDto = new LaonCreateRequestDto();

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));

            given(Laon.of(
                        laonCreateRequestDto.getCenterName(),
                        laonCreateRequestDto.getWallName(),
                        laonCreateRequestDto.getHoldInfo(),
                        laonCreateRequestDto.getVideoUrl(),
                        laonCreateRequestDto.getVideoThumbnailUrl(),
                        laonCreateRequestDto.getContent(),
                        user)).willReturn(laon);

            given(this.laonRepository.save(this.laon)).willReturn(laon);

            // when
            LaonResponseDto laonResponseDto = this.laonService.createLaon("testUserId", laonCreateRequestDto);

            // then
            assertThat(laonResponseDto).isNotNull();
            assertThat(laonResponseDto.getWriter().getId()).isEqualTo("testUserId");
        }
    }
}
