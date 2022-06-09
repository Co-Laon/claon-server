package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private String id;
    private String phoneNumber;
    private String email;
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramId;

    private UserResponseDto(
            String id,
            String phoneNumber,
            String email,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramId
    ) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramId = instagramId;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getNickname(),
                user.getMetropolitanActiveArea(),
                user.getBasicLocalActiveArea(),
                user.getImagePath(),
                user.getInstagramId()
        );
    }
}
