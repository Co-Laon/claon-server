package com.claon.user.web.converter;

import com.claon.common.exception.BadRequestException;
import com.claon.common.exception.ErrorCode;
import com.claon.user.domain.enums.OAuth2Provider;
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
