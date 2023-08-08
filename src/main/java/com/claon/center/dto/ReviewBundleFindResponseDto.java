package com.claon.center.dto;

import com.claon.common.domain.Pagination;
import lombok.Data;

@Data
public class ReviewBundleFindResponseDto {
    private final String centerId;
    private final Double rank;
    private final ReviewFindResponseDto selfReview;
    private final Pagination<ReviewFindResponseDto> otherReviewsPagination;

    private ReviewBundleFindResponseDto(
            String centerId,
            Double rank,
            ReviewFindResponseDto selfReview,
            Pagination<ReviewFindResponseDto> otherReviewsPagination
    ) {
        this.centerId = centerId;
        this.rank = rank;
        this.selfReview = selfReview;
        this.otherReviewsPagination = otherReviewsPagination;
    }

    public static ReviewBundleFindResponseDto from(
            String centerId,
            Double rank,
            ReviewFindResponseDto selfReview,
            Pagination<ReviewFindResponseDto> otherReviewsPagination
    ) {
        return new ReviewBundleFindResponseDto(centerId, rank, selfReview, otherReviewsPagination);
    }
}
