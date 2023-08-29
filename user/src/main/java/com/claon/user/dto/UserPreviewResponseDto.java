package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Data;

@Data
public class UserPreviewResponseDto {
    private String nickname;
    private Boolean isLaon;

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
