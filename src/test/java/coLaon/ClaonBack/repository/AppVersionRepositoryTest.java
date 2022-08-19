package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.version.domain.AppVersion;
import coLaon.ClaonBack.version.repository.AppVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AppVersionRepositoryTest {
    @Autowired
    private AppVersionRepository appVersionRepository;

    private AppVersion appVersion;

    @BeforeEach
    void setUp() {
        // given
        this.appVersion = AppVersion.of(
                "test-android-curr",
                "1.2.3"
        );
        appVersionRepository.save(appVersion);
    }

    @Test
    public void successFindByKey() {
        // given
        String appKey = this.appVersion.getKey();

        // when
        Optional<AppVersion> result = appVersionRepository.findByKey(appKey);

        // then
        assertThat(result).isPresent();
    }
}
