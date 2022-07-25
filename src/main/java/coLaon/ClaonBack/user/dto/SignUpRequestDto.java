package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import coLaon.ClaonBack.common.validator.UserNickname;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @UserNickname
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramOAuthId;
    private String instagramUserName;
}
