package coLaon.ClaonBack;


import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.LaonResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.LaonService;
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
public class LaonServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LaonRepository laonRepository;

    @InjectMocks
    LaonService laonService;

    private User laon;
    private User user;
    private Laon laonRelation;

    @BeforeEach
    void setUp() {
        this.laon = User.of(
                "laonId",
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.user = User.of(
                "userId",
                "test@gmail.com",
                "1234567222",
                "tes2t",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.laonRelation = Laon.of(
                this.laon,
                this.user
        );
    }

    @Test
    @DisplayName("Success case for follow")
    void successFollow() {
        try (MockedStatic<Laon> mockedFollow = mockStatic(Laon.class)) {
            //given

            given(this.userRepository.findById("laonId")).willReturn(Optional.of(laon));
            given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
            given(Laon.of(this.laon, this.user)).willReturn(this.laonRelation);


            given(this.laonRepository.save(this.laonRelation)).willReturn(this.laonRelation);

            //when
            LaonResponseDto laonResponseDto = this.laonService.laon("laonId", "userId");
            //then
            assertThat(laonResponseDto).isNotNull();
            assertThat(laonResponseDto.getLaonId()).isEqualTo("laonId");
            assertThat(laonResponseDto.getUserId()).isEqualTo("userId");
        }
    }

    @Test
    @DisplayName("Success case for unfollow")
    void successUnFollow() {
        try (MockedStatic<Laon> mockedFollow = mockStatic(Laon.class)) {
            //given

            given(this.userRepository.findById("laonId")).willReturn(Optional.of(laon));
            given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
            given(this.laonRepository.findByLaonAndUser(this.laon, this.user)).willReturn(Optional.of(this.laonRelation));

            //when
            LaonResponseDto laonResponseDto = this.laonService.unlaon("laonId", "userId");
            //then
            assertThat(laonResponseDto).isNotNull();
            assertThat(laonResponseDto.getLaonId()).isEqualTo("laonId");
            assertThat(laonResponseDto.getUserId()).isEqualTo("userId");
        }
    }





}

