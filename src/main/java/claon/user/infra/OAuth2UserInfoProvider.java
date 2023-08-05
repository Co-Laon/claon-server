package claon.user.infra;

import claon.user.dto.OAuth2UserInfoDto;

public interface OAuth2UserInfoProvider {
    OAuth2UserInfoDto getUserInfo(String code);
}
