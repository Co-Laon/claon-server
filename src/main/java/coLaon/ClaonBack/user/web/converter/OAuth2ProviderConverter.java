package coLaon.ClaonBack.user.web.converter;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.enums.OAuth2Provider;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class OAuth2ProviderConverter implements Converter<String, OAuth2Provider> {
    @Override
    public OAuth2Provider convert(String source) {
        return Arrays.stream(OAuth2Provider.values())
                .filter(v -> source.equalsIgnoreCase(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.INVALID_OAUTH2_PROVIDER,
                                String.format("'%s' is invalid : not supported", source)
                        )
                );
    }
}
