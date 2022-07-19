package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private String email;
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramOAuthId;
    private String instagramUserName;
    private Boolean isPrivate;

    private UserResponseDto(
            String email,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName,
            Boolean isPrivate
    ) {
        this.email = email;
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
        this.isPrivate = isPrivate;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getMetropolitanActiveArea(),
                user.getBasicLocalActiveArea(),
                user.getImagePath(),
                user.getInstagramOAuthId(),
                user.getInstagramUserName(),
                user.getIsPrivate()
        );
    }
}
