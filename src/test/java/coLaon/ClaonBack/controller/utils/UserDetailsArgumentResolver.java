package coLaon.ClaonBack.controller.utils;

import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.domain.UserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserDetailsArgumentResolver implements HandlerMethodArgumentResolver {
    private final User user;

    public UserDetailsArgumentResolver(User user) {
        super();
        this.user = user;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(UserDetails.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return new UserDetails(user);
    }
}
