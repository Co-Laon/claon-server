package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.common.domain.Pagination;
import lombok.Data;

@Data
public class ReviewBundleFindResponseDto {
    private final String centerId;
    private final Double rank;
    private final ReviewFindResponseDto selfReviewFindResponseDto;
    private final Pagination<ReviewFindResponseDto> otherReviewFindResponseDtoPagination;

    private ReviewBundleFindResponseDto(
            String centerId,
            Double rank,
            ReviewFindResponseDto selfReviewFindResponseDto,
            Pagination<ReviewFindResponseDto> otherReviewFindResponseDtoPagination
    ) {
        this.centerId = centerId;
        this.rank = rank;
        this.selfReviewFindResponseDto = selfReviewFindResponseDto;
        this.otherReviewFindResponseDtoPagination = otherReviewFindResponseDtoPagination;
    }

    public static ReviewBundleFindResponseDto from(
            String centerId,
            Double rank,
            ReviewFindResponseDto selfReviewFindResponseDto,
            Pagination<ReviewFindResponseDto> otherReviewFindResponseDtoPagination
    ) {
        return new ReviewBundleFindResponseDto(centerId, rank, selfReviewFindResponseDto, otherReviewFindResponseDtoPagination);
    }
}
