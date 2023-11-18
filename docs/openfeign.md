## Spring Cloud OpenFeign

- Declarative HTTP Client 도구로써, 쉬운 외부 API 호출 제공
- 장점
  - 코드의 양을 감소하여 개발 편의성 증진
  - 다른 Spring Cloud(Eureka, Circuit Breaker, LoadBalancer)와 통합 용이
- 단점 및 한계
  - 기본 HTTP client가 HTTP2를 지원하지 않음
    - HTTP Client에 추가 설정 필요
  - Reactive 모델을 지원하지 않음
  - 테스트 도구를 제공하지 않음
    - 별도의 설정 파일을 작성을 통한 대응이 필요

### Example
- 의존성 추가
  ```groovy
  implementation "org.springframework.cloud:spring-cloud-starter-openfeign"
  ```
- OpenFeign 활성화
  - @EnableFeignClients을 통해 활성화
  - feign interface들의 위치를 반드시 지정해야 함
    - basePackages 지정
    - clients로 직접 클래스 지정
  ```java
  @EnableFeignClients(basePackages = "com.claon.post")
  @Configuration
  public class FeignConfig {}
  ```
- OpenClient 구현
  - @FeignClient를 통해 구현
  - name 및 url 설정
    - name
      - Feign Client 이름
    - url
      - 호출할 주소의 prefix
      - `spring.cloud.openfeign.client.config.{name}.url`로 설정 가능
  ```java
  @FeignClient(name = "post-service")
  public interface PostClient {
    @GetMapping("/api/v1/histories")
    List<UserPostInfoResponse> findHistoriesByUserId(
            @RequestHeader(name = "X-USER-ID") String userId
    );
  
    @GetMapping("/api/v1/posts/thumbnails")
    Pagination<PostThumbnailResponse> findPostThumbnails(
            @RequestHeader(name = "X-USER-ID") String userId,
            Pageable pageable
    );
  }
  ```

### Config
- code, yaml에 모두 설정값이 존재할 경우, yaml의 정보가 우선순위가 높음
  - 우선순위 변경 가능
- Timeout
  - connectedTimeout
    - TCP handshake timeout
    - default = 1000ms
  - readTimeout
    - 데이터를 읽는데 필요한 시간
    - default = 60000ms
  ```yaml
  spring.cloud.openfeign.client:
    config:
      default:
        connectTimeout: {ms-time}
        readTimeout: {ms-time}
  ```
- Retryer
  - default = Retryer.NEVER_RETRY
  - Retryer는 IOException이 발생한 경우에만 처리
    - 이외의 예외에서 재시도가 필요하다면 Spring-Retry나 ErrorDecoder, Interceptor로 추가적인 구현 필요
  ```java
  @Configuration
  @EnableFeignClients(basePackages = "com.claon.post")
  public class FeignConfig {
    @Bean
    Retryer.Default retryer() {
        // 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대5번 재시도한다.
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }
  }
  ```
- Logging
  - Log level
    - NONE : 로깅하지 않음 (default)
    - BASIC : 요청 메소드, URI의 응답 상태 및 실행시간만 로깅
    - HEADERS : 요청과 응답 헤더 및 기본 정보들을 로깅
    - FULL : 요청과 응답에 대한 헤더, BODY 및 META 데이터 로깅
  ```yaml
  spring.cloud.openfeign.client:
    config:
      default:
        logger-level: {log-level}
  ```
  - DEBUG level으로만 FeignClient 로그를 남길 수 있음
    ```yaml
    logging:
      level:
        {package name}: DEBUG
    ```
  - 다른 level으로 로그를 남기고자 한다면 별도의 설정이 필요
- LocalDate, LocalDateTime, LocalTime
  - 요청 응답 데이터로 LocalDate, LocalDateTime, LocalTime 등이 사용될 경우 Formatter 추가 설정이 필요
  ```java
  @Configuration
  @EnableFeignClients(basePackages = "com.claon.post")
  public class FeignConfig {
    @Bean
    public FeignFormatterRegistrar dateTimeFormatterRegistrar() {
        return registry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setUseIsoFormat(true);
            registrar.registerFormatters(registry);
        };
    }
  }
  ```
- ErrorDecoder
  - ErrorDecoder를 통해 FeignClient에서 발생한 예외를 처리할 수 있음
  ```java
  @EnableFeignClients(basePackages = "com.claon.post")
  @Configuration
  public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new GlobalFeignErrorDecoder();
    }
  }
    
  @Slf4j
  public class GlobalFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse error = getBody(response);
        return new FeignClientException(response.status(), error.errorCode, error.message);
    }

    private ErrorResponse getBody(Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            return new ObjectMapper()
                    .findAndRegisterModules()
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .readValue(bodyIs, ErrorResponse.class);
        } catch (IOException e) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public record ErrorResponse(Integer errorCode, String message, LocalDateTime timeStamp) {}
  }
  ```