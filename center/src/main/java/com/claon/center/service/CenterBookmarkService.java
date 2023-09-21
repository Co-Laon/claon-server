package com.claon.center.service;

import com.claon.center.common.domain.RequestUserInfo;
import com.claon.center.domain.Center;
import com.claon.center.domain.CenterBookmark;
import com.claon.center.dto.CenterBookmarkResponseDto;
import com.claon.center.repository.CenterBookmarkRepository;
import com.claon.center.repository.CenterRepository;
import com.claon.center.common.exception.BadRequestException;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CenterBookmarkService {
    private final CenterRepository centerRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;

    @Transactional
    public CenterBookmarkResponseDto create(
            RequestUserInfo userInfo,
            String centerId
    ) {
        Center center = this.centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        this.centerBookmarkRepository.findByUserIdAndCenterId(userInfo.id(), center.getId()).ifPresent(
                bookmarkCenter -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 즐겨찾기에 등록되어 있습니다."
                    );
                }
        );

        return CenterBookmarkResponseDto.from(
                this.centerBookmarkRepository.save(
                        CenterBookmark.of(
                                center,
                                userInfo.id()
                        )
                ),
                true
        );
    }

    @Transactional
    public CenterBookmarkResponseDto delete(
            RequestUserInfo userInfo,
            String centerId
    ) {
        Center center = this.centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        CenterBookmark bookmarkCenter = this.centerBookmarkRepository.findByUserIdAndCenterId(userInfo.id(), center.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "아직 즐겨찾기에 등록되지 않았습니다."
                )
        );

        this.centerBookmarkRepository.delete(bookmarkCenter);

        return CenterBookmarkResponseDto.from(
                CenterBookmark.of(
                        center,
                        userInfo.id()
                ),
                false
        );
    }
}
