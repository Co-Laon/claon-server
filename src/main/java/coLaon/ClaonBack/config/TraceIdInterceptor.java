package coLaon.ClaonBack.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class TraceIdInterceptor implements HandlerInterceptor {
    /*
    * MDC 를 활용해 traceId를 로거에 추가하는 Interceptor 입니다.
    * */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("response status: {}", response.getStatus());
        MDC.clear();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
