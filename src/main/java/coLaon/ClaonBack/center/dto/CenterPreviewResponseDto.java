package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.CenterImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class CenterPreviewResponseDto {
    private String id;
    private String name;
    private String thumbnailUrl;
    private Double reviewRank;

    @QueryProjection
    public CenterPreviewResponseDto(
            String id,
            String name,
            List<CenterImg> imgList,
            Double reviewRank
    ) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = imgList.get(0).getUrl();
        this.reviewRank = Objects.requireNonNullElse(reviewRank, 0.0);
    }
}

