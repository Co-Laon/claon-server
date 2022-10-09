package coLaon.ClaonBack.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostThumbnailResponseDto {
    private final String postId;
    private final String thumbnailUrl;
    private final String centerName;
    private List<ClimbingHistoryResponseDto> climbingHistories;

    private PostThumbnailResponseDto(
            String postId,
            String thumbnailUrl,
            String centerName,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.thumbnailUrl = thumbnailUrl;
        this.centerName = centerName;
        this.climbingHistories = climbingHistories;
    }

    public static PostThumbnailResponseDto from(
            String postId,
            String thumbnailUrl,
            String centerName,
            List<ClimbingHistoryResponseDto> climbingHistories

    ) {
        return new PostThumbnailResponseDto(
                postId,
                thumbnailUrl,
                centerName,
                climbingHistories
        );
    }
}
