package com.claon.common.utils;

import com.claon.common.domain.JwtDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
public class CookieUtil {
    @Value("${spring.jwt.access-token.name}")
    private String ACCESS_COOKIE_NAME;
    @Value("${spring.jwt.refresh-token.name}")
    private String REFRESH_COOKIE_NAME;
    @Value("${spring.jwt.access-token.expire-seconds}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${spring.jwt.refresh-token.expire-seconds}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    public void createCookie(
            HttpServletResponse res,
            String value,
            String cookieName
    ) {
        int maxAge;
        if (Objects.equals(cookieName, this.ACCESS_COOKIE_NAME))
            maxAge = (int) (this.ACCESS_TOKEN_EXPIRE_TIME / 1000);
        else
            maxAge = (int) (this.REFRESH_TOKEN_EXPIRE_TIME / 1000);

        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);

        res.addCookie(cookie);
    }

    public void addToken(HttpServletResponse response, JwtDto jwt) {
        this.createCookie(response, jwt.getAccessToken(), this.ACCESS_COOKIE_NAME);
        this.createCookie(response, jwt.getRefreshToken(), this.REFRESH_COOKIE_NAME);

        jwt.getIsCompletedSignUp().ifPresent(
                isCompletedSignUp -> this.createCookie(
                        response,
                        isCompletedSignUp.toString(),
                        "isCompletedSignUp"
                ));
    }

    public JwtDto resolveToken(HttpServletRequest request) {
        return JwtDto.of(
                this.getCookie(request, this.ACCESS_COOKIE_NAME)
                        .orElse(null),
                this.getCookie(request, this.REFRESH_COOKIE_NAME)
                        .orElse(null)
        );
    }

    public Optional<String> getCookie(
            HttpServletRequest req,
            String cookieName
    ) {
        if (req.getCookies() == null) return Optional.empty();
        return Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void removeCookie(
            HttpServletResponse res,
            String cookieName
    ) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        res.addCookie(cookie);
    }
}
