package com.claon.gateway.infra;

import com.claon.gateway.dto.OAuth2UserInfoDto;

public interface OAuth2UserInfoProvider {
    OAuth2UserInfoDto getUserInfo(String code);
}
