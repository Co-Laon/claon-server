package claon.center.dto;

import claon.common.utils.RelativeTimeUtil;
import claon.center.domain.CenterReview;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
public class ReviewFindResponseDto {
    private final String reviewId;
    private final String reviewerNickname;
    private final String reviewerProfileImage;
    private final Integer rank;
    private final String content;
    private final String createdAt;
    private final String updatedAt;

    private ReviewFindResponseDto(
            String reviewId,
            String reviewerNickname,
            String reviewerProfileImage,
            Integer rank,
            String content,
            String createdAt,
            String updatedAt
    ) {
        this.reviewId = reviewId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImage = reviewerProfileImage;
        this.rank = rank;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewFindResponseDto from(CenterReview centerReview) {
        return new ReviewFindResponseDto(
                centerReview.getId(),
                centerReview.getWriter().getNickname(),
                centerReview.getWriter().getImagePath(),
                centerReview.getRank(),
                centerReview.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(centerReview.getCreatedAt(), ZoneOffset.ofHours(9))),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(centerReview.getUpdatedAt(), ZoneOffset.ofHours(9)))
        );
    }
}
