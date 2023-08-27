package com.claon.gateway.common.utils;

import com.claon.gateway.common.domain.JwtDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtil {
    @Value("${spring.jwt.access-token.name}")
    private String ACCESS_HEADER_NAME;
    @Value("${spring.jwt.refresh-token.name}")
    private String REFRESH_HEADER_NAME;

    public void createHeader(
            HttpServletResponse res,
            String headerName,
            String value
    ) {
        res.addHeader(headerName, value);
    }

    private String getHeader(
            HttpServletRequest request,
            String headerName
    ) {
        return request.getHeader(headerName);
    }

    public void addToken(
            HttpServletResponse res,
            JwtDto jwtDto
    ) {
        this.createHeader(res, this.ACCESS_HEADER_NAME, jwtDto.getAccessToken());
        this.createHeader(res, this.REFRESH_HEADER_NAME, jwtDto.getRefreshToken());
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return this.getHeader(request, this.ACCESS_HEADER_NAME);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return this.getHeader(request, this.REFRESH_HEADER_NAME);
    }
}
