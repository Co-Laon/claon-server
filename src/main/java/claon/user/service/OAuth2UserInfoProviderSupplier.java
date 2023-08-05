package claon.user.service;

import claon.user.infra.GoogleUserInfoProvider;
import claon.user.infra.KakaoUserInfoProvider;
import claon.user.infra.OAuth2UserInfoProvider;
import claon.user.domain.enums.OAuth2Provider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2UserInfoProviderSupplier {
    private final Map<OAuth2Provider, OAuth2UserInfoProvider> supplier = new HashMap<>();

    private final GoogleUserInfoProvider googleUserInfoProvider;
    private final KakaoUserInfoProvider kakaoUserInfoProvider;

    public OAuth2UserInfoProviderSupplier(
            GoogleUserInfoProvider googleUserInfoProvider,
            KakaoUserInfoProvider kakaoUserInfoProvider
    ) {
        this.googleUserInfoProvider = googleUserInfoProvider;
        this.kakaoUserInfoProvider = kakaoUserInfoProvider;
    }

    @PostConstruct
    public void init() {
        this.supplier.put(OAuth2Provider.GOOGLE, this.googleUserInfoProvider);
        this.supplier.put(OAuth2Provider.KAKAO, this.kakaoUserInfoProvider);
    }

    public OAuth2UserInfoProvider getProvider(OAuth2Provider provider) {
        return this.supplier.get(provider);
    }
}
