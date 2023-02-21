package coLaon.ClaonBack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomServletWrappingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

        chain.doFilter(wrappingRequest, wrappingResponse);

        log.info("request : {} {}", request.getMethod(), request.getRequestURI());
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            if (!request.getHeader("Content-Type").contains("multipart/form-data")) {
                ObjectMapper om = new ObjectMapper();
                log.info("request body : {}", om.readTree(wrappingRequest.getContentAsByteArray()));
            }
        }

        wrappingResponse.copyBodyToResponse();
    }
}
