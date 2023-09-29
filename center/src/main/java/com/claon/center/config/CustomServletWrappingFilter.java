package com.claon.center.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class CustomServletWrappingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

        chain.doFilter(wrappingRequest, wrappingResponse);

        if (!request.getRequestURI().contains("actuator")) {
            log.info("request : {} {}", request.getMethod(), request.getRequestURI());
            if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
                if (!Optional.ofNullable(request.getHeader("Content-Type"))
                        .map(header -> header.contains("multipart/form-data"))
                        .orElse(false)) {
                    ObjectMapper om = new ObjectMapper();
                    log.info("request body : {}", om.readTree(wrappingRequest.getContentAsByteArray()));
                }
            }
        }

        wrappingResponse.copyBodyToResponse();
    }
}
