## Spring Cloud Gateway

### 1. 개요
- Spring Cloud Gateway는 Spring과 Java 위에서 API Gateway를 구축하기 위한 라이브러리를 제공하며,
다양한 기준에 따라 유연한 라우팅 요청 방식을 제공한다.
- Security, Resilency 및 모니터링 등의 cross-cutting concern에 중점을 둔 라이브러리
- Spring Cloud Gateway는 tomcat이 아닌 Netty를 통해 요청이 들어오는 형식인데, 기본적으로 1개의 Request에 1개의 Thread를 할당하는 tomcat으로는 모든 요청이 통과하는
gateway의 특징으로 인해 성능적인 이슈가 발생할 수 있다. Netty는 비동기 방식의 WAS로, 1개의 Thread에 다수의 Request를 할당할 수 있으므로, 기존 방식보다 더 많은 요청을 처리할 수 있다.

### 1.1 spring cloud gateway 사용 이유
- springboot 2.4 버전 미만까지 netflix zuul을 지원해왔다. 하지만 현재 Netflix Zuul은 개발 및 지원을 중단하였으며(maintenance mode), 오랜 기간 동안 새로운 기능 및 보안 업데이트를 받지 못할 수 있다.
  - https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode
- 그에 반해, Spring Cloud Gateway(SCG)는 지속적으로 개발되고 업데이트 되고있으며, 공식 블로그에서 Replacement로 Spring Cloud Gateway이 선택되었다.
- Zuul은 Netflix OSS에 포함되어있으며, Spring Application과 조합하려면 별도의 통합 작업이 필요하다. 하지만,
Spring Cloud Gateway는 Spring Framework와 완벽하게 통합되므 별도의 통합 작업이 필요하지 않다.
- Zuul은 tomcat을 사용하지만, Spring Cloud Gateway는 Netty를 사용함으로써 동기 요청 처리를 개선할 수 있다는 장점이 있다.
- Zuul은 Filter로만 동작하고, Spring Cloud Gateway는 Predicates + Filter를 조합하여 동작한다.

### 2. API Gateway
- API Gateway는 Back-end 서비스에 대한 API 트래픽을 수락, 변환, 라우팅 및 관맇는 중개자 역할을 하는 서비스(혹은 장치, 프록시)를 의미한다.
- 많은 서비스들이 MSA형태로 구성되면서, 변경에 따른 영향을 최소화를 하는 형태를 지녔는데, 이 과정에서 개발과 배포를 할 수 있다는 장점을 지녔으나, 
작은 단위의 서비스가 많아질수록 공통적으로 들어가는 인증/인가, 로깅과 같은 기능들을 중복으로 개발해야한다는 문제점이 발생한다.
- 이러한 문제점을 해결하기 위해 등장하였으며, API Gateway는 아래 그림과 같이 클라이언트와 각 서비스들 사이에 위치하게 된다.

### 3. Spring Cloud Gateway 구성도
- Spring Cloud Gateway에 대한 구성은 아래 그림과 같다.

<img src="./images/spring-cloud-gateway-architecture.png" width="800" height="480">

- Client가 Spring Cloud Gateway 서버로 요청을 보낸다.
- Gateway Handler Mapping에서 Request가 매핑된다고 판단하면, Gateway Web Handler로 보낸다.
- Gateway Web Handler는 매핑되는 요청을 위한 Filter chain을 거쳐서 요청을 실행한다.
- Spring Cloud Gateway는 시스템 분류 구조에 따라 Predicate 영역과 Filter Chain 영역으로 구분되어 있다.

#### 3.1 Spring Cloud Gateway - Predicate
- 주어진 Request가 설정된 조건을 충족하는지 테스트하는 구성요소로, 각 요청 경로에 대해 충족하게 되는 경우, 하나 이상의 조건자를 정의할 수 있다.
- 설정한 Predicate가 매칭되지 않으면, HTTP 404 Response를 반환받는다.
- 관련자료: https://cloud.spring.io/spring-cloud-gateway/multi/multi_gateway-request-predicates-factories.html


#### 3.2 Spring Cloud Gateway - Filter
- Gateway를 기준으로 들어오는 요청 및 나가는 응답에 대해, 수정을 가능하게 해주는 요소로
RewritePath, AuthenticationHeaderFilter 등을 구성할 수 있다.
- 관련자료: https://cloud.spring.io/spring-cloud-gateway/multi/multi__gatewayfilter_factories.html

#### 3.3 예시
```yml
spring:
  config:
    activate:
      on-profile: local

// .. 

  cloud:
    discovery:
      enabled: true
    loadbalancer:
      configurations: health-check
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: auth
          uri: lb://AUTH
          predicates: 
            - Path=/auth/**
          filters: 
            - RewritePath=/auth/(?<segment>.*), /$\{segment}
            - AuthenticationHeaderFilter // custom filter !!
```
- lb: 해당 route의 고유 식별자를 의미
- uri: 해당 Route의 주소를 의미로, localhost로 설정할 수도 있으나, Eureka 에서 로드밸런싱을 하기위해 lb:{Application-Name}으로 구성할 수 있다.
  - 여기서 {Application-Name}은 Eureka Dashboard에 나와있는 Eureka Client 이름을 의미한다.
- Claon-server project 기준으로, 위에 Predicate에 적혀있는 것을 보았을 때, 요청 Path가 "/auth/"로 된 요청인지를 확인하는 것을 의미한다.
- filters에 구성된 "RewritePath"는 "/auth/abc"라고 매칭되는 요청을 "/abc"로 바꾸어서 요청이 전달된다는 의미를 가지며, "AuthenticationHeaderFilter"는 프로젝트에서 직접 정의한
CustomFilter로, 원하는 기능을 수행하기 위해 구현하여 직접 등록된 filter로 아래와 같이 설정할 수 있다.

```java
@Slf4j
@Component
public class AuthenticationHeaderFilter extends AbstractGatewayFilterFactory<AuthenticationHeaderFilter.Config> {
    private final HeaderUtil headerUtil;
    private final JwtUtil jwtUtil;

    public AuthenticationHeaderFilter(HeaderUtil headerUtil, JwtUtil jwtUtil) {
        super(Config.class);
        this.headerUtil = headerUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Authentication Filter...");

            String token = this.headerUtil.resolveAccessToken(exchange.getRequest());
            String userId = this.jwtUtil.getUserId(token);

            log.info("Request Client Id : " + userId);

            addAuthorizationHeaders(exchange.getRequest(), userId);

            return chain.filter(exchange);
        };
    }

    // tomcat과 달리, 비동기 서버인 Netty에서는 Request/Response 객체를 선언할 때, Server를 앞에 붙인다.
    private void addAuthorizationHeaders(ServerHttpRequest request, String userId) {
        request.mutate()
                .header("X-USER-ID", userId)
                .build();
    }
}
```

- 위와 같은 gateway custom filter는 route별로 적용하는 것이 아닌 공통적으로 실행되는 필터로, 모든 필터의 가장 첫번째로 실행이 되고, 가장 마지막에 종료가 된다.
- 또한, 아래와 같은 코드를 추가함으로써, filter에서 Error Handling을 할 수 있다.

```java
    @Bean
    public ErrorWebExceptionHandler tokenValidation() {
        return new JwtTokenExceptionHandler();
    }

    public static class JwtTokenExceptionHandler implements ErrorWebExceptionHandler {
        private String getErrorCode(int errorCode) {
            return "{\"errorCode\":" + errorCode + "}";
        }

        @Override
        public Mono<Void> handle(
                ServerWebExchange exchange, Throwable ex
        ) {
            int errorCode = 500;
            if (ex.getClass() == NullPointerException.class) {
                errorCode = 100;
            } else if (ex.getClass() == ExpiredJwtException.class) {
                errorCode = 200;
            }

            byte[] bytes = getErrorCode(errorCode).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }
```

- 기본적으로 Spring MVC에서는 ErrorController, AbstractErrorController를 기본적으로 제공하지만, Spring cloud는 Webflux기반으므로 해당 핸들러를 사용할 수 없다.
- WebFlux에서 제공하는 Error Handler는 ErrorWebExceptionHandler, AbstractErrorWebExceptionHandler, DefaultErrorWebExceptionHandler가 있다.