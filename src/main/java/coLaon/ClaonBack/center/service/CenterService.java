package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.CenterReport;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.domain.enums.CenterSearchOption;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterDetailResponseDto;
import coLaon.ClaonBack.center.dto.CenterHoldInfoResponseDto;
import coLaon.ClaonBack.center.dto.CenterNameResponseDto;
import coLaon.ClaonBack.center.dto.CenterPostThumbnailResponseDto;
import coLaon.ClaonBack.center.dto.CenterPreviewResponseDto;
import coLaon.ClaonBack.center.dto.CenterReportCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterReportResponseDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.repository.CenterBookmarkRepository;
import coLaon.ClaonBack.center.repository.CenterReportRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.CenterRepositorySupport;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.center.repository.ReviewRepositorySupport;
import coLaon.ClaonBack.center.repository.SectorInfoRepository;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.validator.IsAdminValidator;
import coLaon.ClaonBack.user.domain.User;
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
    private final PostPort postPort;
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final CenterReportRepository centerReportRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public CenterResponseDto create(
            User admin,
            CenterCreateRequestDto requestDto
    ) {
        IsAdminValidator.of(admin.getEmail()).validate();

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
    public CenterDetailResponseDto findCenter(User user, String centerId) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        Boolean isBookmarked = centerBookmarkRepository.findByUserIdAndCenterId(user.getId(), centerId).isPresent();
        Integer postCount = postPort.countByCenterExceptBlockUser(centerId, user.getId());
        Integer reviewCount = reviewRepositorySupport.countByCenterExceptBlockUser(centerId, user.getId());

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
            User user,
            CenterSearchOption option,
            Pageable pageable
    ) {
        return paginationFactory.create(
                centerRepositorySupport.findCenterByOption(user.getId(), option, pageable)
        );
    }

    @Transactional
    public CenterReportResponseDto createReport(
            User user,
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
                                user,
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
            User user,
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

            return this.postPort.findByCenterAndHoldExceptBlockUser(center.getId(), holdId.get(), user.getId(), pageable);
        }

        return this.postPort.findByCenterExceptBlockUser(center.getId(), user.getId(), pageable);
    }
}
