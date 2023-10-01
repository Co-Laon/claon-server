package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserPreviewResponseDto {
    private final String nickname;
    private final Boolean isLaon;

    private UserPreviewResponseDto(
            String nickname,
            Boolean isLaon
    ) {
        this.nickname = nickname;
        this.isLaon = isLaon;
    }

    public static UserPreviewResponseDto from(
            User user,
            Boolean isLaon
    ) {
        return new UserPreviewResponseDto(user.getNickname(), isLaon);
    }
}
