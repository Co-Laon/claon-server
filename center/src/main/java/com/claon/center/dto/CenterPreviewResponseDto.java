package com.claon.center.dto;

import com.claon.center.domain.CenterImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
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
        this.reviewRank = Optional.ofNullable(reviewRank).orElse(0.0);
    }
}

