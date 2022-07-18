package coLaon.ClaonBack.user.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserModifyRequestDto {

    @Size(max=100, message="닉네임은 20자 이하여야 합니다. ")
    private String nickname;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramUserName;
    private String instagramOAuthId;
    private Boolean isPrivate;

    public UserModifyRequestDto(
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName,
            Boolean isPrivate
    ) {
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
        this.isPrivate = isPrivate;
    }
}
