package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.dto.ClimbingHistoryResponseDto;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

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

    public static PostThumbnailResponseDto from(Post post) {
        return new PostThumbnailResponseDto(
                post.getId(),
                post.getThumbnailUrl(),
                post.getCenter().getName(),
                post.getClimbingHistorySet().stream().map(
                                climbingHistory -> ClimbingHistoryResponseDto.from(
                                        HoldInfoResponseDto.from(climbingHistory.getHoldInfo()),
                                        climbingHistory.getClimbingCount()
                                ))
                        .collect(Collectors.toList())
        );
    }
}
