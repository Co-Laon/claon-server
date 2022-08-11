package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.Center;
import lombok.Data;

@Data
public class CenterPreviewResponseDto {
    private String id;
    private String name;
    private String thumbnailUrl;
    private Float reviewRank;

    private CenterPreviewResponseDto(
            String id,
            String name,
            String thumbnailUrl,
            Float reviewRank
    ) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.reviewRank = reviewRank;
    }

    public static CenterPreviewResponseDto from(Center center) {
        return new CenterPreviewResponseDto(
                center.getId(),
                center.getName(),
                center.getThumbnailUrl(),
                center.getReviewRank()
        );
    }
}

