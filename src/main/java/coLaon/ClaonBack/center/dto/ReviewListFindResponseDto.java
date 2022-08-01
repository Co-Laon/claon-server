package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.common.domain.Pagination;
import lombok.Data;

@Data
public class ReviewListFindResponseDto {
    private final String centerId;
    private final float rank;
    private final Pagination<ReviewFindResponseDto> reviewFindResponseDtoPagination;

    private ReviewListFindResponseDto(Pagination<ReviewFindResponseDto> reviewFindResponseDtoPagination, String centerId, float rank) {
        this.reviewFindResponseDtoPagination = reviewFindResponseDtoPagination;
        this.centerId = centerId;
        this.rank = rank;
    }

    public static ReviewListFindResponseDto from(Pagination<ReviewFindResponseDto> reviewFindResponseDtoPagination, Center center) {
        return new ReviewListFindResponseDto(reviewFindResponseDtoPagination, center.getId(), center.getReviewRank());
    }
}
