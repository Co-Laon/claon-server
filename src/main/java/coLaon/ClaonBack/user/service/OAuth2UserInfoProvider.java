package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.user.dto.OAuth2UserInfoDto;

public interface OAuth2UserInfoProvider {
    OAuth2UserInfoDto getUserInfo(String code);
}
