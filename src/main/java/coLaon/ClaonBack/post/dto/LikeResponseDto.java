package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.LaonLike;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class LikeResponseDto {
    private String id;
    private String userId;
    private String postId;
    private String likeNumber;

    public LikeResponseDto(String id, User liker, Post post) {
        this.id = id;
        this.userId = liker.getId();
        this.postId = post.getId();
    }

    public static LikeResponseDto from(LaonLike like) {
        return new LikeResponseDto(like.getId(), like.getLiker(), like.getPost());
    }
}
