package com.claon.center.service;

import com.claon.center.common.domain.RequestUserInfo;
import com.claon.center.domain.Center;
import com.claon.center.domain.CenterReview;
import com.claon.center.dto.*;
import com.claon.center.repository.CenterRepository;
import com.claon.center.repository.ReviewRepository;
import com.claon.center.repository.ReviewRepositorySupport;
import com.claon.center.common.domain.PaginationFactory;
import com.claon.center.common.exception.BadRequestException;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.NotFoundException;
import com.claon.center.common.validator.IdEqualValidator;
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
            RequestUserInfo userInfo,
            String centerId,
            ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        this.reviewRepository.findByUserIdAndCenterId(userInfo.id(), center.getId()).ifPresent(
                review -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 작성된 리뷰가 있습니다."
                    );
                }
        );

        return ReviewResponseDto.from(
                reviewRepository.save(
                        CenterReview.of(
                                reviewCreateRequestDto.getRank(),
                                reviewCreateRequestDto.getContent(),
                                userInfo.id(),
                                center
                        )
                )
        );
    }

    @Transactional
    public ReviewResponseDto updateReview(
            RequestUserInfo userInfo,
            String reviewId,
            ReviewUpdateRequestDto updateRequestDto
    ) {
        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "리뷰를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriterId(), userInfo.id()).validate();

        review.update(updateRequestDto.getRank(), updateRequestDto.getContent());

        return ReviewResponseDto.from(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponseDto deleteReview(
            RequestUserInfo userInfo,
            String reviewId
    ) {
        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "리뷰를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriterId(), userInfo.id()).validate();

        reviewRepository.delete(review);

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public ReviewBundleFindResponseDto findReview(
            RequestUserInfo userInfo,
            String centerId,
            Pageable pageable
    ) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        return ReviewBundleFindResponseDto.from(
                center.getId(),
                reviewRepositorySupport.findRankByCenterExceptBlockUser(center.getId(), userInfo.id()),
                reviewRepository.findByUserIdAndCenterId(userInfo.id(), center.getId())
                        .map(ReviewFindResponseDto::from)
                        .orElse(null),
                this.paginationFactory.create(
                        reviewRepositorySupport.findByCenterExceptBlockUserAndSelf(center.getId(), userInfo.id(), pageable)
                                .map(ReviewFindResponseDto::from)
                )
        );
    }
}
