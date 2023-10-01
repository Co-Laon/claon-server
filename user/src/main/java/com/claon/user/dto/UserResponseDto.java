package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserResponseDto {
    private final String email;
    private final String nickname;
    private final Float height;
    private final Float armReach;
    private final Float apeIndex;

    private UserResponseDto(
            String email,
            String nickname,
            Float height,
            Float armReach,
            Float apeIndex
    ) {
        this.email = email;
        this.nickname = nickname;
        this.height = height;
        this.armReach = armReach;
        this.apeIndex = apeIndex;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight()
        );
    }
}
