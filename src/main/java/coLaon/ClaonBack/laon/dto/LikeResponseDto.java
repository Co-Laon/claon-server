package coLaon.ClaonBack.laon.dto;

import coLaon.ClaonBack.laon.domain.Laon;
import coLaon.ClaonBack.laon.domain.LaonLike;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class LikeResponseDto {
    private String id;
    private String userId;
    private String laonId;
    private String likeNumber;

    public LikeResponseDto(String id, User liker, Laon laon) {
        this.id = id;
        this.userId = liker.getId();
        this.laonId = laon.getId();
    }

    public static LikeResponseDto from(LaonLike like) {
        return new LikeResponseDto(like.getId(), like.getLiker(), like.getLaon());
    }
}
