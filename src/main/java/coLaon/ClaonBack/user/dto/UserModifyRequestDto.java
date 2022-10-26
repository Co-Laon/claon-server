package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import coLaon.ClaonBack.common.validator.UserNickname;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyRequestDto {
    @UserNickname(message = "잘못된 닉네임 입니다.")
    private String nickname;
    private Float height;
    private Float armReach;
    private String imagePath;
    private String instagramUserName;
    private String instagramOAuthId;

    public Optional<Float> getHeight() {
        return Optional.ofNullable(this.height);
    }

    public Optional<Float> getArmReach() {
        return Optional.ofNullable(this.armReach);
    }
}
