package claon.user.dto;

import claon.user.domain.User;
import lombok.Data;

@Data
public class UserPreviewResponseDto {
    private String nickname;
    private String imagePath;
    private Boolean isLaon;

    private UserPreviewResponseDto(
            String nickname,
            String imagePath,
            Boolean isLaon
    ) {
        this.nickname = nickname;
        this.imagePath = imagePath;
        this.isLaon = isLaon;
    }

    public static UserPreviewResponseDto from(
            User user,
            Boolean isLaon
    ) {
        return new UserPreviewResponseDto(user.getNickname(), user.getImagePath(), isLaon);
    }
}
