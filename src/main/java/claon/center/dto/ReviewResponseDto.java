package claon.center.dto;

import claon.center.domain.CenterReview;
import lombok.Data;

@Data
public class ReviewResponseDto {
    private final String reviewId;
    private final Integer rank;
    private final String content;
    private final String centerId;

    private ReviewResponseDto(String reviewId, Integer rank, String content, String centerId) {
        this.reviewId = reviewId;
        this.rank = rank;
        this.content = content;
        this.centerId = centerId;
    }

    public static ReviewResponseDto from(CenterReview centerReview) {
        return new ReviewResponseDto(
                centerReview.getId(),
                centerReview.getRank(),
                centerReview.getContent(),
                centerReview.getCenter().getId()
        );
    }
}
