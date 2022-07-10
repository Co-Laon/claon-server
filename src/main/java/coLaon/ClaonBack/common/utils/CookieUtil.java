package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.config.dto.JwtDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
public class CookieUtil {
    @Value("${spring.jwt.access-token.cookie-name}")
    private String ACCESS_COOKIE_NAME;
    @Value("${spring.jwt.refresh-token.cookie-name}")
    private String REFRESH_COOKIE_NAME;
    @Value("${spring.jwt.access-token.expire-seconds}")
    private Integer ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${spring.jwt.refresh-token.expire-seconds}")
    private Integer REFRESH_TOKEN_EXPIRE_TIME;

    public void createCookie(
            HttpServletResponse res,
            String cookieName,
            String value
    ) {
        int maxAge;
        if (Objects.equals(cookieName, this.ACCESS_COOKIE_NAME))
            maxAge = this.ACCESS_TOKEN_EXPIRE_TIME;
        else
            maxAge = this.REFRESH_TOKEN_EXPIRE_TIME;

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
                isCompletedSignUp -> {
                    this.createCookie(response, jwt.getIsCompletedSignUp().toString(), "isCompletedSignUp");
                }
        );
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
        if(req.getCookies() == null) return Optional.empty();
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
