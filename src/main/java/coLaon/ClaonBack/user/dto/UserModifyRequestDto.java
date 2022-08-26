package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.common.domain.enums.MetropolitanArea;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import coLaon.ClaonBack.common.validator.UserNickname;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyRequestDto {
    @UserNickname(message = "잘못된 닉네임 입니다.")
    private String nickname;
    private MetropolitanArea metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramUserName;
    private String instagramOAuthId;
}
