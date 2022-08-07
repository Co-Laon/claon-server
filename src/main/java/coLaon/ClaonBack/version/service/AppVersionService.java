package coLaon.ClaonBack.version.service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.version.domain.AppStore;
import coLaon.ClaonBack.version.domain.AppVersion;
import coLaon.ClaonBack.version.dto.AppVersionFindResponseDto;
import coLaon.ClaonBack.version.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppVersionService {
    private final AppVersionRepository appVersionRepository;

    @Transactional(readOnly = true)
    public AppVersionFindResponseDto findVersion(String store) {
        AppVersion appVersion = this.appVersionRepository.findByKey(AppStore.of(store)).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s 버전이 존재하지 않습니다.", store)
                )
        );

        return AppVersionFindResponseDto.from(
                appVersion
        );
    }
}
