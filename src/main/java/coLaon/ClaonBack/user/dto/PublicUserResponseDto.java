package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class PublicUserResponseDto {
    private String nickname;
    private Long postCount;
    private Long laonCount;
    private Long likeCount;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private Boolean isLaon;
    private Boolean isPrivate;


    private PublicUserResponseDto(User user, boolean isLaon, Long postCount, Long laonCount, Long likeCount) {
        this.nickname = user.getNickname();
        this.isLaon = isLaon;
        this.postCount = postCount;
        this.laonCount = laonCount;
        this.likeCount = likeCount;
        this.isPrivate = user.getIsPrivate();

        // Only set when private is false.
        if (!user.getIsPrivate()) {
            this.metropolitanActiveArea = user.getMetropolitanActiveArea();
            this.basicLocalActiveArea = user.getBasicLocalActiveArea();
            this.imagePath = user.getImagePath();
        }
    }

    public static PublicUserResponseDto from(User user, boolean isLaon,  Long postCount, Long laonCount, Long likeCount) {
        return new PublicUserResponseDto(user, isLaon, postCount, laonCount, likeCount);
    }
}
