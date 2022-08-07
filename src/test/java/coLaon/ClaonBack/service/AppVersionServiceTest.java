package coLaon.ClaonBack.service;

import coLaon.ClaonBack.version.domain.AppVersion;
import coLaon.ClaonBack.version.dto.AppVersionFindResponseDto;
import coLaon.ClaonBack.version.repository.AppVersionRepository;
import coLaon.ClaonBack.version.service.AppVersionService;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class AppVersionServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    AppVersionRepository appVersionRepository;

    @InjectMocks
    AppVersionService appVersionService;

    private User user;
    private AppVersion appVersion;

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

        ReflectionTestUtils.setField(this.user, "id", "userId");

        this.appVersion = AppVersion.of(
                "aos",
                "1.2.3"
        );
    }

    @Test
    @DisplayName("Success case for find any version")
    void successFindVersion() {
        // given
        given(this.appVersionRepository.findByKey("aos")).willReturn(Optional.of(appVersion));

        // when
        AppVersionFindResponseDto responseDto = this.appVersionService.findVersion("aos");

        // then
        assertThat(responseDto)
                .extracting("key", "version")
                .contains("aos", "1.2.3");
    }
}
