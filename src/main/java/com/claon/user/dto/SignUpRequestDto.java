package com.claon.user.dto;

import com.claon.common.validator.UserNickname;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
