package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import coLaon.ClaonBack.common.validator.UserNickname;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @UserNickname(message = "잘못된 닉네임 입니다.")
    private String nickname;
    private Float height;
    private Float armReach;
    private String imagePath;
    private String instagramOAuthId;
    private String instagramUserName;
}
