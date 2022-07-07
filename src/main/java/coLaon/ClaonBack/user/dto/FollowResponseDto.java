package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.Follow;
import lombok.Data;

@Data
public class FollowResponseDto {
    private final String followerId;
    private final String followingId;

    private FollowResponseDto(
            String followerId,
            String followingId
    ) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public static FollowResponseDto from(
            Follow follow
    ) {
        return new FollowResponseDto(
                follow.getFollower().getId(),
                follow.getFollowing().getId()
        );
    }
}
