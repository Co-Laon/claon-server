package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterReview;
import coLaon.ClaonBack.center.dto.ReviewCreateRequestDto;
import coLaon.ClaonBack.center.dto.ReviewFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewListFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewResponseDto;
import coLaon.ClaonBack.center.dto.ReviewUpdateRequestDto;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.ReviewRepository;
import coLaon.ClaonBack.center.repository.ReviewRepositorySupport;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CenterReviewService {
    private final CenterRepository centerRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRepositorySupport reviewRepositorySupport;
    private final PaginationFactory paginationFactory;

    @Transactional
    public ReviewResponseDto createReview(
            User user,
            String centerId,
            ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        this.reviewRepository.findByUserIdAndCenterId(user.getId(), center.getId()).ifPresent(
                review -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 작성된 리뷰가 존재합니다."
                    );
                }
        );

        return ReviewResponseDto.from(
                reviewRepository.save(
                        CenterReview.of(
                                reviewCreateRequestDto.getRank(),
                                reviewCreateRequestDto.getContent(),
                                user,
                                center
                        )
                )
        );
    }

    @Transactional
    public ReviewResponseDto updateReview(
            User user,
            String reviewId,
            ReviewUpdateRequestDto updateRequestDto
    ) {
        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "리뷰 정보를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriter().getId(), user.getId()).validate();

        review.update(updateRequestDto.getRank(), updateRequestDto.getContent());

        return ReviewResponseDto.from(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponseDto deleteReview(
            User user,
            String reviewId
    ) {
        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "리뷰 정보를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriter().getId(), user.getId()).validate();

        reviewRepository.delete(review);

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public ReviewListFindResponseDto findReview(
            User user,
            String centerId,
            Pageable pageable
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        return ReviewListFindResponseDto.from(
                this.paginationFactory.create(
                        reviewRepositorySupport.findByCenterExceptBlockUser(center.getId(), user.getId(), pageable)
                                .map(ReviewFindResponseDto::from)
                ),
                center,
                reviewRepositorySupport.findRankByCenterExceptBlockUser(center.getId(), user.getId())
        );
    }
}
