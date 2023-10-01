package com.claon.user.dto.request;

import com.claon.user.dto.validator.UserNickname;

public record UserModifyRequestDto(
        @UserNickname(message = "잘못된 닉네임 입니다.")
        String nickname,
        Float height,
        Float armReach
) {
}
