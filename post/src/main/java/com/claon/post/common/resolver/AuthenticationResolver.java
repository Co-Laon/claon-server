package com.claon.post.common.resolver;

import com.claon.post.common.annotation.RequestUser;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

public class AuthenticationResolver implements HandlerMethodArgumentResolver {
    private static final String REQUEST_USER_ID_HEADER = "X-USER-ID";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        return Optional.ofNullable(webRequest.getHeader(REQUEST_USER_ID_HEADER))
                .map(RequestUserInfo::new)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.NOT_SIGN_IN, "Not found authentication header"));
    }
}
