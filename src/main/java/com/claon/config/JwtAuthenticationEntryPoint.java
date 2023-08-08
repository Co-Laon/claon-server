package com.claon.config;

import com.claon.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        // API Access with invalid JWT
        if (errorCode == null || errorCode == ErrorCode.INVALID_JWT) {
            this.setResponse(response, ErrorCode.INVALID_JWT, "다시 로그인 해주세요.");
        }

        // API Access without JWT
        if (errorCode == ErrorCode.NOT_SIGN_IN) {
            this.setResponse(response, ErrorCode.NOT_SIGN_IN, "로그인 해주세요.");
        }

        // User not found
        if (errorCode == ErrorCode.USER_DOES_NOT_EXIST) {
            this.setResponse(response, ErrorCode.USER_DOES_NOT_EXIST, "이용자를 찾을 수 없습니다.");
        }
    }

    private void setResponse(
            HttpServletResponse response,
            ErrorCode errorCode,
            String message
    ) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                "{" +
                        "\"errorCode\" : \"" + errorCode.getCode() + "\"," +
                        "\"message\" : \"" + message + "\"," +
                        "\"timeStamp\" : \"" + LocalDateTime.now() + "\"" +
                        "}"
        );
    }
}

