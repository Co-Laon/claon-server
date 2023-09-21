package com.claon.center.dto;

import com.claon.center.domain.CenterReview;
import com.claon.center.common.utils.RelativeTimeUtil;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
public class ReviewFindResponseDto {
    private final String reviewId;
    private final String reviewerId;
    private final Integer rank;
    private final String content;
    private final String createdAt;
    private final String updatedAt;

    private ReviewFindResponseDto(
            String reviewId,
            String reviewerId,
            Integer rank,
            String content,
            String createdAt,
            String updatedAt
    ) {
        this.reviewId = reviewId;
        this.reviewerId = reviewerId;
        this.rank = rank;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewFindResponseDto from(CenterReview centerReview) {
        return new ReviewFindResponseDto(
                centerReview.getId(),
                centerReview.getWriterId(),
                centerReview.getRank(),
                centerReview.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(centerReview.getCreatedAt(), ZoneOffset.ofHours(9))),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(centerReview.getUpdatedAt(), ZoneOffset.ofHours(9)))
        );
    }
}
