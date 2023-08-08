package com.claon.version.service;

import com.claon.common.exception.BadRequestException;
import com.claon.common.exception.ErrorCode;
import com.claon.version.repository.AppVersionRepository;
import com.claon.version.domain.enums.AppStore;
import com.claon.version.domain.AppVersion;
import com.claon.version.dto.AppVersionFindResponseDto;
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
