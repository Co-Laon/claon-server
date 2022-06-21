package coLaon.ClaonBack.laon.dto;

import coLaon.ClaonBack.laon.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;
import lombok.Getter;

@Data
public class LaonResponseDto {
    private String centerName;
    private String wallName;
    private String holdInfo;
    private String videoUrl;
    private String videoThumbnailUrl;
    private String content;
    private Boolean isDeleted;
    private User writer;

    private LaonResponseDto(
            String centerName,
            String wallName,
            String holdInfo,
            String videoUrl,
            String videoThumbnailUrl,
            String content,
            User writer
    ) {
        this.centerName = centerName;
        this.wallName = wallName;
        this.holdInfo = holdInfo;
        this.videoUrl = videoUrl;
        this.videoThumbnailUrl = videoThumbnailUrl;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
    }

    public static LaonResponseDto from(Laon laon) {
        return new LaonResponseDto(
                laon.getCenterName(),
                laon.getWallName(),
                laon.getHoldInfo(),
                laon.getVideoUrl(),
                laon.getVideoThumbnailUrl(),
                laon.getContent(),
                laon.getWriter()
        );
    }
}
