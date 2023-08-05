package claon.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPostThumbnailResponseDto {
    private final String postId;
    private final String thumbnailUrl;
    private final String centerName;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private UserPostThumbnailResponseDto(
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

    public static UserPostThumbnailResponseDto from(
            String postId,
            String thumbnailUrl,
            String centerName,
            List<ClimbingHistoryResponseDto> climbingHistories

    ) {
        return new UserPostThumbnailResponseDto(
                postId,
                thumbnailUrl,
                centerName,
                climbingHistories
        );
    }
}
