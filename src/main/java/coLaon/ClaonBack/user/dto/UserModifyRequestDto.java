package coLaon.ClaonBack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyRequestDto {
    @NotBlank
    @Size(max=100, message="닉네임은 20자 이하여야 합니다. ")
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramUserName;
    private String instagramOAuthId;
}
