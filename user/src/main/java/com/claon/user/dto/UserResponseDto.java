package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private String email;
    private String nickname;
    private Float height;
    private Float armReach;
    private Float apeIndex;
    private Boolean isPrivate;

    private UserResponseDto(
            String email,
            String nickname,
            Float height,
            Float armReach,
            Float apeIndex,
            Boolean isPrivate
    ) {
        this.email = email;
        this.nickname = nickname;
        this.height = height;
        this.armReach = armReach;
        this.apeIndex = apeIndex;
        this.isPrivate = isPrivate;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight(),
                user.getIsPrivate()
        );
    }
}
