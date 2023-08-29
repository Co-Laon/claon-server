package com.claon.auth.dto;

import com.claon.auth.common.validator.UserNickname;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SignUpRequestDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
    @UserNickname
    private String nickname;
    private Float height;
    private Float armReach;
}
