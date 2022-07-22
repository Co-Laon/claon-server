package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.CenterReview;
import lombok.Data;

@Data
public class ReviewResponseDto {
    private final String reviewId;
    private final Integer rank;
    private final String content;
    private final String centerId;
    private final Boolean isDeleted;

    private ReviewResponseDto(String reviewId, Integer rank, String content, String centerId, Boolean isDeleted) {
        this.reviewId = reviewId;
        this.rank = rank;
        this.content = content;
        this.centerId = centerId;
        this.isDeleted = isDeleted;
    }

    public static ReviewResponseDto from(CenterReview centerReview) {
        return new ReviewResponseDto(
                centerReview.getId(),
                centerReview.getRank(),
                centerReview.getContent(),
                centerReview.getCenter().getId(),
                centerReview.getIsDeleted()
        );
    }
}
