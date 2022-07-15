package coLaon.ClaonBack.config;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.utils.CookieUtil;
import coLaon.ClaonBack.common.utils.JwtUtil;
import coLaon.ClaonBack.config.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        JwtDto jwtDto = this.cookieUtil.resolveToken((HttpServletRequest) request);

        if (jwtDto.getAccessToken() != null && jwtDto.getRefreshToken() != null) {
            if (this.jwtUtil.validateToken(jwtDto.getAccessToken())) {
                if (this.jwtUtil.validateToken(jwtDto.getRefreshToken())) {
                    // Success sign-in
                    Authentication auth = this.jwtUtil.getAuthentication(jwtDto.getAccessToken());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    // Fail sign in because expire refresh token
                    request.setAttribute("exception", ErrorCode.INVALID_JWT);
                }
            } else {
                if (this.jwtUtil.validateToken(jwtDto.getRefreshToken())) {
                    // Success sign-in and create access and refresh token
                    String userId = this.jwtUtil.getUserId(jwtDto.getRefreshToken());

                    this.cookieUtil.addToken((HttpServletResponse) response, this.jwtUtil.createToken(userId));

                    Authentication auth = this.jwtUtil.getAuthentication(jwtDto.getAccessToken());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    // Fail sign in because expire access and refresh token
                    request.setAttribute("exception", ErrorCode.INVALID_JWT);
                }
            }
        } else {
            // no tokens
            request.setAttribute("exception", ErrorCode.NOT_SIGN_IN);
        }

        chain.doFilter(request, response);
    }
}
