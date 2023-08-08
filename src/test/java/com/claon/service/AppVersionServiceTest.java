package com.claon.service;

import com.claon.version.domain.AppVersion;
import com.claon.version.domain.enums.AppStore;
import com.claon.version.dto.AppVersionFindResponseDto;
import com.claon.version.repository.AppVersionRepository;
import com.claon.version.service.AppVersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AppVersionServiceTest {
    @Mock
    AppVersionRepository appVersionRepository;

    @InjectMocks
    AppVersionService appVersionService;

    private AppVersion appVersion;

    @BeforeEach
    void setUp() {
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
        AppVersionFindResponseDto responseDto = this.appVersionService.findVersion(AppStore.GOOGLE);

        // then
        assertThat(responseDto)
                .extracting("key", "version")
                .contains("aos", "1.2.3");
    }
}
