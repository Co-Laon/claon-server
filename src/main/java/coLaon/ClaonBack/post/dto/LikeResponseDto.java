package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostLike;
import lombok.Data;

@Data
public class LikeResponseDto {
    private String postId;
    private Integer likeNumber;

    private LikeResponseDto(
            String postId,
            Integer likeNumber
    ) {
        this.postId = postId;
        this.likeNumber = likeNumber;
    }

    public static LikeResponseDto from(
            PostLike like,
            Integer likeNumber
    ) {
        return new LikeResponseDto(
                like.getPost().getId(),
                likeNumber
        );
    }
}