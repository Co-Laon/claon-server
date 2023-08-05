package claon.version.service;

import claon.common.exception.BadRequestException;
import claon.common.exception.ErrorCode;
import claon.version.repository.AppVersionRepository;
import claon.version.domain.enums.AppStore;
import claon.version.domain.AppVersion;
import claon.version.dto.AppVersionFindResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppVersionService {
    private final AppVersionRepository appVersionRepository;

    @Transactional(readOnly = true)
    public AppVersionFindResponseDto findVersion(AppStore store) {
        AppVersion appVersion = this.appVersionRepository.findByKey(store.getValue()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s 버전을 찾을 수 없습니다.", store)
                )
        );

        return AppVersionFindResponseDto.from(
                appVersion
        );
    }
}
