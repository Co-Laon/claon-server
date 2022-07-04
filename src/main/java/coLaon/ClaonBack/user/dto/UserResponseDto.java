package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private String id;
    private String email;
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramOAuthId;

    private UserResponseDto(
            String id,
            String email,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getMetropolitanActiveArea(),
                user.getBasicLocalActiveArea(),
                user.getImagePath(),
                user.getInstagramOAuthId()
        );
    }
}
