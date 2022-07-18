package coLaon.ClaonBack.service;

import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
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
        this.laon = User.createNewUser(
                "laonId",
                "test@gmail.com",
                "1234567890",
                "userNickname1",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.user = User.createNewUser(
                "userId",
                "test@gmail.com",
                "1234567222",
                "userNickname2",
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
    @DisplayName("Success case for create laon")
    void successCreateLaon() {
        try (MockedStatic<Laon> mockedLaon = mockStatic(Laon.class)) {
            // given
            given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(laon));
            given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
            given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.empty());

            mockedLaon.when(() -> Laon.of(this.laon, this.user)).thenReturn(this.laonRelation);

            given(this.laonRepository.save(this.laonRelation)).willReturn(this.laonRelation);

            // when
            this.laonService.createLaon("userNickname1", "userId");

            // then
            assertThat(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Success case for delete laon")
    void successDeleteLaon() {
        // given
        given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(laon));
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
        given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.of(this.laonRelation));

        // when
        this.laonService.deleteLaon("userNickname1", "userId");

        // then
        assertThat(this.laonRepository.findAll()).isEmpty();
    }
}