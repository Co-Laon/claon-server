package coLaon.ClaonBack.user.domain;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

@Getter
public enum OAuth2Provider {
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String value;

    OAuth2Provider(String value) {
        this.value = value;
    }

    public static OAuth2Provider of(String value) {
        return Arrays.stream(values())
                .filter(v -> value.equalsIgnoreCase(v.value))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.INVALID_OAUTH2_PROVIDER,
                                String.format("'%s' is invalid : not supported", value)
                        )
                );
    }
}
