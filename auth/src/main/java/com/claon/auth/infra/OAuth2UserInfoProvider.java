package com.claon.auth.infra;

import com.claon.auth.dto.OAuth2UserInfoDto;

public interface OAuth2UserInfoProvider {
    OAuth2UserInfoDto getUserInfo(String code);
}
