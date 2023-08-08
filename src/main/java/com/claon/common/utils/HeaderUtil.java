package com.claon.common.utils;

import com.claon.common.domain.JwtDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public String getHeader(
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

        jwtDto.getIsCompletedSignUp().ifPresent(
                isCompletedSignUp -> this.createHeader(
                        res,
                        "isCompletedSignUp",
                        isCompletedSignUp.toString()
                )
        );
    }

    public JwtDto resolveToken(HttpServletRequest request) {
        return JwtDto.of(
                this.getHeader(request, this.ACCESS_HEADER_NAME),
                this.getHeader(request, this.REFRESH_HEADER_NAME)
        );
    }
}
