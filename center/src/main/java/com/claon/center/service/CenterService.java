package com.claon.center.service;

import com.claon.center.common.domain.RequestUserInfo;
import com.claon.center.domain.*;
import com.claon.center.domain.enums.CenterSearchOption;
import com.claon.center.dto.*;
import com.claon.center.dto.request.CenterRequestDto;
import com.claon.center.dto.request.CenterReportRequestDto;
import com.claon.center.repository.*;
import com.claon.center.common.domain.Pagination;
import com.claon.center.common.domain.PaginationFactory;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.NotFoundException;
import com.claon.center.service.client.PostClient;
import com.claon.center.service.client.dto.PostThumbnailResponse;
import lombok.RequiredArgsConstructor;
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
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final CenterReportRepository centerReportRepository;
    private final PaginationFactory paginationFactory;

    private final PostClient postClient;

    @Transactional
    public CenterResponseDto create(
            RequestUserInfo userInfo,
            CenterRequestDto requestDto
    ) {
        Center center = this.centerRepository.save(
                Center.of(
                        requestDto.name(),
                        requestDto.address(),
                        requestDto.tel(),
                        requestDto.webUrl(),
                        requestDto.instagramUrl(),
                        requestDto.youtubeUrl(),
                        requestDto.imgList(),
                        requestDto.operatingTimeList(),
                        requestDto.facilities(),
                        requestDto.chargeList(),
                        requestDto.holdInfoImg()
                )
        );

        return CenterResponseDto.from(
                center,
                requestDto.holdInfoList()
                        .stream()
                        .map(holdInfo -> this.holdInfoRepository.save(
                                HoldInfo.of(
                                        holdInfo.name(),
                                        holdInfo.img(),
                                        center
                                )))
                        .collect(Collectors.toList()),
                requestDto.sectorInfoList()
                        .stream()
                        .map(dto -> this.sectorInfoRepository.save(
                                SectorInfo.of(
                                        dto.name(),
                                        dto.start(),
                                        dto.end(),
                                        center
                                )))
                        .collect(Collectors.toList())
        );
    }

    @Transactional(readOnly = true)
    public CenterDetailResponseDto findCenter(RequestUserInfo userInfo, String centerId) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        Boolean isBookmarked = centerBookmarkRepository.findByUserIdAndCenterId(userInfo.id(), centerId).isPresent();
        Long postCount = postClient.countPostsByCenterId(userInfo.id(), centerId);
        Long reviewCount = reviewRepositorySupport.countByCenterExceptBlockUser(centerId, userInfo.id());

        return CenterDetailResponseDto.from(
                center,
                holdInfoRepository.findAllByCenter(center),
                sectorInfoRepository.findAllByCenter(center),
                isBookmarked,
                postCount,
                reviewCount
        );
    }

    @Transactional(readOnly = true)
    public List<HoldInfoResponseDto> findHoldInfoByCenterId(
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
                .map(HoldInfoResponseDto::from)
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
            RequestUserInfo userInfo,
            CenterSearchOption option,
            Pageable pageable
    ) {
        return paginationFactory.create(
                centerRepositorySupport.findCenterByOption(userInfo.id(), option, pageable)
        );
    }

    @Transactional
    public CenterReportResponseDto createReport(
            RequestUserInfo userInfo,
            String centerId,
            CenterReportRequestDto centerReportRequestDto
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
                                centerReportRequestDto.content(),
                                centerReportRequestDto.reportType(),
                                userInfo.id(),
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
    public Pagination<PostThumbnailResponse> getCenterPosts(
            RequestUserInfo userInfo,
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

        holdId.ifPresent(s -> holdInfoRepository.findByIdAndCenter(s, center).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "홀드를 찾을 수 없습니다."
                )
        ));

        return this.postClient.findPostThumbnails(userInfo.id(), centerId, holdId, pageable);
    }
}
