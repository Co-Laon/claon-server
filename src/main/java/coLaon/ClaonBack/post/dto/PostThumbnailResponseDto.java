package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.Post;
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

    public static PostThumbnailResponseDto from(Post post) {
        return new PostThumbnailResponseDto(
                post.getId(),
                post.getThumbnailUrl()
        );
    }

}
