package com.claon.gateway.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtil {
    private final String TOKEN_PREFIX = "Bearer ";

    @Value("${spring.jwt.access-token.name}")
    private String ACCESS_HEADER_NAME;

    private String getHeader(
            ServerHttpRequest request,
            String headerName
    ) {
        return request.getHeaders().getFirst(headerName);
    }

    public String resolveAccessToken(ServerHttpRequest request) {
        String bearerToken = this.getHeader(request, this.ACCESS_HEADER_NAME);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return bearerToken;
    }
}
