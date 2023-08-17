package com.claon.center.service;

import com.claon.center.domain.*;
import com.claon.center.domain.enums.CenterSearchOption;
import com.claon.center.dto.*;
import com.claon.center.repository.*;
import com.claon.center.common.domain.Pagination;
import com.claon.center.common.domain.PaginationFactory;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.NotFoundException;
import com.claon.center.common.validator.IsAdminValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterService {
    private final CenterRepository centerRepository;
    private final CenterRepositorySupport centerRepositorySupport;
    private final HoldInfoRepository holdInfoRepository;
    private final SectorInfoRepository sectorInfoRepository;
    private final ReviewRepositorySupport reviewRepositorySupport;
//    private final PostPort postPort;
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final CenterReportRepository centerReportRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public CenterResponseDto create(
            String adminId,
            CenterCreateRequestDto requestDto
    ) {
//        IsAdminValidator.of(admin.getEmail()).validate();

        Center center = this.centerRepository.save(
                Center.of(
                        requestDto.getName(),
                        requestDto.getAddress(),
                        requestDto.getTel(),
                        requestDto.getWebUrl(),
                        requestDto.getInstagramUrl(),
                        requestDto.getYoutubeUrl(),
                        requestDto.getImgList()
                                .stream().map(dto -> CenterImg.of(dto.getUrl()))
                                .collect(Collectors.toList()),
                        requestDto.getOperatingTimeList()
                                .stream().map(dto -> OperatingTime.of(dto.getDay(), dto.getStart(), dto.getEnd()))
                                .collect(Collectors.toList()),
                        requestDto.getFacilities(),
                        requestDto.getChargeList()
                                .stream()
                                .map(dto -> Charge.of(dto.getChargeList().stream()
                                                .map(chargeElement -> ChargeElement.of(
                                                        chargeElement.getName(),
                                                        chargeElement.getFee()))
                                                .collect(Collectors.toList()),
                                        dto.getImage()))
                                .collect(Collectors.toList()),
                        requestDto.getHoldInfoImg()
                )
        );

        return CenterResponseDto.from(
                center,
                requestDto.getHoldInfoList()
                        .stream()
                        .map(holdInfo -> this.holdInfoRepository.save(
                                HoldInfo.of(
                                        holdInfo.getName(),
                                        holdInfo.getImg(),
                                        center
                                )))
                        .collect(Collectors.toList()),
                requestDto.getSectorInfoList()
                        .stream()
                        .map(dto -> this.sectorInfoRepository.save(
                                SectorInfo.of(
                                        dto.getName(),
                                        dto.getStart(),
                                        dto.getEnd(),
                                        center
                                )))
                        .collect(Collectors.toList())
        );
    }

    @Transactional(readOnly = true)
    public CenterDetailResponseDto findCenter(String userId, String centerId) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        Boolean isBookmarked = centerBookmarkRepository.findByUserIdAndCenterId(userId, centerId).isPresent();
//        Integer postCount = postPort.countByCenterExceptBlockUser(centerId, userId);
        Integer reviewCount = reviewRepositorySupport.countByCenterExceptBlockUser(centerId, userId);

        return CenterDetailResponseDto.from(
                center,
                holdInfoRepository.findAllByCenter(center),
                sectorInfoRepository.findAllByCenter(center),
                isBookmarked,
                0,
                reviewCount
        );
    }

    @Transactional(readOnly = true)
    public List<CenterHoldInfoResponseDto> findHoldInfoByCenterId(
            String centerId
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        return holdInfoRepository.findAllByCenter(center)
                .stream()
                .map(CenterHoldInfoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CenterNameResponseDto> searchCenterName(String keyword) {
        return centerRepository.searchCenter(keyword)
                .stream()
                .map(CenterNameResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Pagination<CenterPreviewResponseDto> findCenterListByOption(
            String userId,
            CenterSearchOption option,
            Pageable pageable
    ) {
        return paginationFactory.create(
                centerRepositorySupport.findCenterByOption(userId, option, pageable)
        );
    }

    @Transactional
    public CenterReportResponseDto createReport(
            String userId,
            String centerId,
            CenterReportCreateRequestDto centerReportCreateRequestDto
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        return CenterReportResponseDto.from(
                this.centerReportRepository.save(
                        CenterReport.of(
                                centerReportCreateRequestDto.getContent(),
                                centerReportCreateRequestDto.getReportType(),
                                userId,
                                center
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public Pagination<CenterPreviewResponseDto> searchCenter(
            String name,
            Pageable pageable
    ) {
        return paginationFactory.create(
                centerRepositorySupport.searchCenter(name, pageable)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<CenterPostThumbnailResponseDto> getCenterPosts(
            String userId,
            String centerId,
            Optional<String> holdId,
            Pageable pageable
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        if (holdId.isPresent()) {
            holdInfoRepository.findByIdAndCenter(holdId.get(), center).orElseThrow(
                    () -> new NotFoundException(
                            ErrorCode.DATA_DOES_NOT_EXIST,
                            "홀드를 찾을 수 없습니다."
                    )
            );

//            return this.postPort.findByCenterAndHoldExceptBlockUser(center.getId(), holdId.get(), userId, pageable);
            return paginationFactory.create(new PageImpl<>(List.of(), pageable, 0));
        }

//        return this.postPort.findByCenterExceptBlockUser(center.getId(), userId, pageable);
        return paginationFactory.create(new PageImpl<>(List.of(), pageable, 0));
    }
}
