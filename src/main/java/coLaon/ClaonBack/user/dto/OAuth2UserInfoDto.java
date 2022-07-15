package coLaon.ClaonBack.user.dto;

import lombok.Getter;

@Getter
public class OAuth2UserInfoDto {
    private final String oAuthId;
    private final String email;

    private OAuth2UserInfoDto(
            String oAuthId,
            String email
    ) {
        this.oAuthId = oAuthId;
        this.email = email;
    }

    public static OAuth2UserInfoDto of(
            String oAuthId,
            String email
    ) {
        return new OAuth2UserInfoDto(
                oAuthId,
                email
        );
    }
}
