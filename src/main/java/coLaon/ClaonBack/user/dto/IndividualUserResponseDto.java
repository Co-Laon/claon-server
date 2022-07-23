package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class IndividualUserResponseDto {
    private String nickname;
    private Long postCount;
    private Long laonCount;
    private Long likeCount;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramUrl;
    private Boolean isLaon;
    private Boolean isPrivate;


    private IndividualUserResponseDto(User user, boolean isLaon, Long postCount, Long laonCount, Long likeCount) {
        this.nickname = user.getNickname();
        this.isLaon = isLaon;
        this.postCount = postCount;
        this.laonCount = laonCount;
        this.likeCount = likeCount;
        this.isPrivate = user.getIsPrivate();
        this.imagePath = user.getImagePath();

        // Only set when private is false.
        if (!user.getIsPrivate()) {
            this.metropolitanActiveArea = user.getMetropolitanActiveArea();
            this.basicLocalActiveArea = user.getBasicLocalActiveArea();
            if (user.getInstagramUserName() != null) {
                this.instagramUrl = "https://instagram.com/" + user.getInstagramUserName();
            }
        }
    }

    public static IndividualUserResponseDto from(User user, boolean isLaon, Long postCount, Long laonCount, Long likeCount) {
        return new IndividualUserResponseDto(user, isLaon, postCount, laonCount, likeCount);
    }
}
