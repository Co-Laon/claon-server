package com.claon.center.dto;

import com.claon.common.domain.Pagination;
import com.claon.center.domain.Center;
import lombok.Data;

@Data
public class ReviewListFindResponseDto {
    private final String centerId;
    private final Double rank;
    private final Pagination<ReviewFindResponseDto> reviewFindResponseDtoPagination;

    private ReviewListFindResponseDto(Pagination<ReviewFindResponseDto> reviewFindResponseDtoPagination, String centerId, Double rank) {
        this.reviewFindResponseDtoPagination = reviewFindResponseDtoPagination;
        this.centerId = centerId;
        this.rank = rank;
    }

    public static ReviewListFindResponseDto from(Pagination<ReviewFindResponseDto> reviewFindResponseDtoPagination, Center center, Double rank) {
        return new ReviewListFindResponseDto(reviewFindResponseDtoPagination, center.getId(), rank);
    }
}