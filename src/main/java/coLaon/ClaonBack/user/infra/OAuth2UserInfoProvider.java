package coLaon.ClaonBack.user.infra;

import coLaon.ClaonBack.user.dto.OAuth2UserInfoDto;

public interface OAuth2UserInfoProvider {
    OAuth2UserInfoDto getUserInfo(String code);
}
