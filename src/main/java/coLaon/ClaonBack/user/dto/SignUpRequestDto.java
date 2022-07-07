package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 20, message = "닉네임은 2자에서 20자 입력 가능합니다.")
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramOAuthId;
    private String instagramUserName;
}
