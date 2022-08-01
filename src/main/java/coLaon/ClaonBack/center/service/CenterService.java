package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.CenterReview;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterDetailResponseDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.CenterSearchResponseDto;
import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.center.dto.ReviewCreateRequestDto;
import coLaon.ClaonBack.center.dto.ReviewFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewListFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewResponseDto;
import coLaon.ClaonBack.center.dto.ReviewUpdateRequestDto;
import coLaon.ClaonBack.center.repository.BookmarkCenterRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.common.validator.IsAdminValidator;
import coLaon.ClaonBack.center.repository.ReviewRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterService {
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final HoldInfoRepository holdInfoRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final BookmarkCenterRepository bookmarkCenterRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public CenterResponseDto create(
            String userId,
            CenterCreateRequestDto requestDto
    ) {
        User admin = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

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
                                .stream().map(dto -> Charge.of(dto.getName(), dto.getFee()))
                                .collect(Collectors.toList()),
                        requestDto.getChargeImg(),
                        requestDto.getHoldInfoImg(),
                        requestDto.getSectorInfoList()
                                .stream().map(dto -> SectorInfo.of(dto.getName(), dto.getStart(), dto.getEnd()))
                                .collect(Collectors.toList())
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
                        .collect(Collectors.toList())
        );
    }

    @Transactional(readOnly = true)
    public CenterDetailResponseDto findCenter(String userId, String centerId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        boolean isBookmarked = bookmarkCenterRepository.findByUserIdAndCenterId(userId, centerId).isPresent();
        Integer postCount = postRepository.selectCountByCenter(centerId, userId);
        Integer reviewCount = reviewRepository.selectCountByCenter(centerId, userId);

        return CenterDetailResponseDto.from(
                center,
                holdInfoRepository.findAllByCenter(center),
                isBookmarked,
                postCount,
                reviewCount
        );
    }

    @Transactional(readOnly = true)
    public List<HoldInfoResponseDto> findHoldInfoByCenterId(String userId, String centerId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        return holdInfoRepository.findAllByCenter(center)
                .stream()
                .map(HoldInfoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CenterSearchResponseDto> searchCenter(String userId, String keyword) {
        userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        return centerRepository.searchCenter(keyword)
                .stream()
                .map(CenterSearchResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDto createReview(
            String userId,
            String centerId,
            ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        this.reviewRepository.findByUserIdAndCenterId(writer.getId(), center.getId()).ifPresent(
                review -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 작성된 리뷰가 존재합니다."
                    );
                }
        );

        Integer reviewCount = reviewRepository.countByCenter(center);
        List<Integer> ranks = reviewRepository.selectRanksByCenterId(centerId);
        center.addRank(ranks, reviewCreateRequestDto.getRank(), reviewCount);

        return ReviewResponseDto.from(
                reviewRepository.save(
                        CenterReview.of(
                                reviewCreateRequestDto.getRank(),
                                reviewCreateRequestDto.getContent(),
                                writer,
                                center
                        )
                )
        );
    }

    @Transactional
    public ReviewResponseDto updateReview(
            String userId,
            String reviewId,
            ReviewUpdateRequestDto updateRequestDto
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "리뷰 정보를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriter().getId(), writer.getId()).validate();

        Integer reviewCount = reviewRepository.countByCenter(review.getCenter());
        List<Integer> ranks = reviewRepository.selectRanksByCenterId(review.getCenter().getId());
        review.getCenter().changeRank(ranks, review.getRank(), updateRequestDto.getRank(), reviewCount);

        review.update(updateRequestDto.getRank(), updateRequestDto.getContent());

        return ReviewResponseDto.from(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponseDto deleteReview(
            String userId,
            String reviewId
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "리뷰 정보를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriter().getId(), writer.getId()).validate();

        Integer reviewCount = reviewRepository.countByCenter(review.getCenter());
        List<Integer> ranks = reviewRepository.selectRanksByCenterId(review.getCenter().getId());
        review.getCenter().deleteRank(ranks, review.getRank(), reviewCount);

        reviewRepository.delete(review);

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public ReviewListFindResponseDto findReview(String userId, String centerId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        List<Integer> ranks = reviewRepository.selectRanksByCenterId(centerId);
        center.updateRank((float) ranks.stream().mapToInt(r -> r).average().orElse(0));

        return ReviewListFindResponseDto.from(
                this.paginationFactory.create(
                        reviewRepository.findByCenter(center.getId(), userId, pageable)
                                .map(ReviewFindResponseDto::from)
                ),
                center
        );
    }
}
