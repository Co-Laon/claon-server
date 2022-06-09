package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phoneNumber;
    @Email(message = "올바른 EMAIL형식이 아닙니다.")
    private String email;
    @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자입니다.")
    private String password;
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 20, message = "닉네임은 2자에서 20자입니다.")
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramId;
}
