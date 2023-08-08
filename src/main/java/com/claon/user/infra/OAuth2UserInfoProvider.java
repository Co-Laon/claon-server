package com.claon.user.infra;

import com.claon.user.dto.OAuth2UserInfoDto;

public interface OAuth2UserInfoProvider {
    OAuth2UserInfoDto getUserInfo(String code);
}
