package coLaon.ClaonBack.center.dto;

import lombok.Data;

@Data
public class PostThumbnailResponseDto {
    private final String postId;
    private final String thumbnailUrl;

    private PostThumbnailResponseDto(
            String postId,
            String thumbnailUrl
    ) {
        this.postId = postId;
        this.thumbnailUrl = thumbnailUrl;

    }

    public static PostThumbnailResponseDto from(String id, String thumbnailUrl) {
        return new PostThumbnailResponseDto(
                id,
                thumbnailUrl
        );
    }
}
