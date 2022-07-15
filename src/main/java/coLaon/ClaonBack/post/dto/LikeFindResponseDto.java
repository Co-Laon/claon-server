package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostLike;
import lombok.Data;

@Data
public class LikeFindResponseDto {
    private String postId;
    private String likerNickname;
    private String likerProfileImage;
    private Integer likeNumber;

    private LikeFindResponseDto(
            String postId,
            String likerNickname,
            String likerProfileImage,
            Integer likeNumber
    ) {
        this.postId = postId;
        this.likerNickname = likerNickname;
        this.likerProfileImage = likerProfileImage;
        this.likeNumber = likeNumber;
    }

    public static LikeFindResponseDto from(PostLike postLike, Integer likeNumber) {
        return new LikeFindResponseDto(
                postLike.getPost().getId(),
                postLike.getLiker().getNickname(),
                postLike.getLiker().getImagePath(),
                likeNumber
        );
    }
}
