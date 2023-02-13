package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import coLaon.ClaonBack.common.validator.UserNickname;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SignUpRequestDto {
    @UserNickname
    private String nickname;
    private Float height;
    private Float armReach;
    private String imagePath;
    private String instagramOAuthId;
    private String instagramUserName;
}
