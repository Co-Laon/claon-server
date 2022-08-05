package coLaon.ClaonBack.version.service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
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
    public AppVersionFindResponseDto findAppleVersion(String key) {
        AppVersion appVersion = this.appVersionRepository.findByKey(key).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "앱스토어 버전이 존재하지 않습니다."
                )
        );

        return AppVersionFindResponseDto.from(
                appVersion
        );
    }

    @Transactional(readOnly = true)
    public AppVersionFindResponseDto findAndroidVersion(String key) {
        AppVersion appVersion = this.appVersionRepository.findByKey(key).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "플레이스토어 버전이 존재하지 않습니다."
                )
        );

        return AppVersionFindResponseDto.from(
                appVersion
        );
    }
}
