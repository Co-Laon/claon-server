package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.user.domain.OAuth2Provider;
import coLaon.ClaonBack.user.infra.GoogleUserInfoProvider;
import coLaon.ClaonBack.user.infra.InstagramUserInfoProvider;
import coLaon.ClaonBack.user.infra.KakaoUserInfoProvider;
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
