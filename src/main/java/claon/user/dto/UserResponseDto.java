package claon.user.dto;

import claon.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private String email;
    private String nickname;
    private Float height;
    private Float armReach;
    private Float apeIndex;
    private String imagePath;
    private String instagramOAuthId;
    private String instagramUserName;
    private Boolean isPrivate;

    private UserResponseDto(
            String email,
            String nickname,
            Float height,
            Float armReach,
            Float apeIndex,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName,
            Boolean isPrivate
    ) {
        this.email = email;
        this.nickname = nickname;
        this.height = height;
        this.armReach = armReach;
        this.apeIndex = apeIndex;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
        this.isPrivate = isPrivate;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight(),
                user.getImagePath(),
                user.getInstagramOAuthId(),
                user.getInstagramUserName(),
                user.getIsPrivate()
        );
    }
}
