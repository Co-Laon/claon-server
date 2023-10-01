package com.claon.center.dto;

import com.claon.center.common.domain.Pagination;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CenterReviewResponseDto {
    private final String centerId;
    private final Double rank;
    private final ReviewDetailResponseDto selfReview;
    private final Pagination<ReviewDetailResponseDto> otherReviewsPagination;

    private CenterReviewResponseDto(
            String centerId,
            Double rank,
            ReviewDetailResponseDto selfReview,
            Pagination<ReviewDetailResponseDto> otherReviewsPagination
    ) {
        this.centerId = centerId;
        this.rank = rank;
        this.selfReview = selfReview;
        this.otherReviewsPagination = otherReviewsPagination;
    }

    public static CenterReviewResponseDto from(
            String centerId,
            Double rank,
            ReviewDetailResponseDto selfReview,
            Pagination<ReviewDetailResponseDto> otherReviewsPagination
    ) {
        return new CenterReviewResponseDto(centerId, rank, selfReview, otherReviewsPagination);
    }
}
