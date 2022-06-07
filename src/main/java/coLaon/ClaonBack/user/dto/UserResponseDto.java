package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private String phoneNumber;
    private String email;
    private String nickname;
    private String wideActiveArea;
    private String narrowActiveArea;
    private String imagePath;
    private String instagramId;

    private UserResponseDto(String phoneNumber, String email, String nickname, String wideActiveArea, String narrowActiveArea, String imagePath, String instagramId) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.wideActiveArea = wideActiveArea;
        this.narrowActiveArea = narrowActiveArea;
        this.imagePath = imagePath;
        this.instagramId = instagramId;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getPhoneNumber(),
                user.getEmail(),
                user.getNickname(),
                user.getWideActiveArea(),
                user.getNarrowActiveArea(),
                user.getImagePath(),
                user.getInstagramId()
        );
    }
}
