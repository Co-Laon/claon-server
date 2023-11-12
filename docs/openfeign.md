## Spring Cloud OpenFeign

### 1. 개요
- Declarative(선언적인)* HTTP Client 도구로써, 외부 API 호출을 쉽게할 수 있도록 도와준다.
  - Declarative : 어노테이션 사용을 의미하며 OpenFeign은 인터페이스에 어노테이션만 추가하여 구현이 가능
- Spring Data JPA와 유사한 방식이며 개발 편의성을 높일 수 있다.
- 장점
  - 인터페이스와 어노테이션을 기반으로 작성해야하는 코드의 양을 감소하여 개발의 편의성을 증진
  - Spring MVC 구조를 어노테이션을 활용한 개발이 가능
  - 다른 Spring cloud(Eureka, Circuit Breaker, LoadBalancer)와 통합이 용이
- 단점 및 한계
  - 기본 HTTP client가 HTTP2를 지원하지 않음
    - HTTP Client에 추가 설정 필요
  - 공식적으로 Reactive 모델을 지원하지 않음
    - 비공식 오픈소스 라이브러리로 사용 가능
  - 경우에 따라 초기화 에러가 발생할 수 있음
    - Object Provider로 대응 필요
  - 테스트 도구를 제공하지 않음
    - 별도의 설정 파일을 작성을 통한 대응이 필요

### 2. Usage Setting
- 의존성 추가
```
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}
```
- OpenFeign 활성화
  - Main 클래스에 @Enable 어노테이션을 붙여주는 것은 SpringBoot가 제공하는 테스트에 영향을 줄 수 있어 별도의 Config 파일을 만들어주는 것이 바람직하다.
  - 별도의 파일로 설정할 경우에는 feign 인터페이스들의 위치를 반드시 지정해야 하며 basePackages로 지정해주거나, clients로 직접 클래스들을 지정해도 된다.
  - 일반적으로 아래와 같이 패키지로 지정하면 된다.
```java
@EnableFeignClients(basePackages = "com.claon.user")
@Configuration
public class FeignConfig {
}
```
- OpenClient 구현
  - API 호출을 수행할 클라이언트는 인터페이스에 @FeignClient 어노테이션을 붙여주면 된다.
  - name 와 url 설정이 필요하다.
    - name : 임의의 Client 이름이며 SpringCloud LoadBalancer Client를 만드는데 사용
    - url : 호출할 주소
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

### 3. Config Setting
- 기본 설정
  - yml과 java config 모두 가능하며 둘 다 설정값이 존재할 경우 yml의 정보가 java config보다 높은 우선순위를 가진다.
  - 우선순위에 대한 설정 변경이 가능하다.
  - @Configuration 어노테이션이 붙은 설정 클래스를 FeignClient에 붙여 클라이언트별 설정이 가능하다.
- 타임아웃(Timeout) 설정
  - default 값은 connectedTimeout = 1000ms, readTimeout = 60000ms
  - 아래와 같이 별도의 설정이 가능(단위 : ms)
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: {ms-time}
        readTimeout: {ms-time}
```
- 재시도(Retry) 설정
  - default 값으로 Retryer.NEVER_RETRY을 등록하여 Retry를 시도하지 않는다.
  - Feign이 제공하는 Retryer는 IOException이 발생한 경우에만 처리되므로 이외의 경우에도 재시도가 필요하다면 Spring-Retry나 에러디코더, 인터셉터로 추가적인 구현이 필요하다.
  - 재시도를 적용하여 수많은 동시 요청을 보내 장애를 유발하는 Retry Storm을 일으킬 수 있어 주의가 필요하다.
  - 재시도 설정과 관련된 예시 코드
  ```java
  @Configuration
  @EnableFeignClients(basePackages = "post-service")
  class OpenFeignConfig {
    @Bean
    Retryer.Default retryer() {
    // 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대5번 재시도한다.
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }
  }
  ```
- 요청/응답 로깅(Logging) 설정
  - Logger의 이름은 전체 인터페이스 이름이며 Feign Client마다 생성된다.
  - Feign은 4가지 로그 수준(Log level)을 제공한다.
    - NONE : 로깅하지 않음(default)
    - BASIC : 요청 메소드, URI의 응답 상태 및 실행시간만 로깅
    - HEADERS : 요청과 응답 헤더 및 기본 정보들을 로깅
    - FULL : 요청과 응답에 대한 헤더, BODY 및 META 데이터 로깅
    - 로그 수준 등록 예시 코드
    ```java
    @Configuration
    @EnableFeignClients(basePackages = "post-service")
    class OpenFeignConfig {
        @Bean
        Logger.Level feignLoggerLevel() {
            return Logger.Level.BASIC;
        }
    }
    ```
    - Feign은 DEBUG 수준으로만 로그를 남길 수 있어 반드시 로그 레벨을 DEBUG로 설정
      - 클라이언트별 적용 가능
      - 설정 파일에서 아래와 같이 수정 필요
      ```yaml
      logging:
        level:
          {package name}: DEBUG
      ```
    - INFO 수준으로 로그를 남기고자 한다면 별도의 로깅 설정이 필요
      - Feign이 제공하는 Logger를 확장하거나 인터셉터를 사용하여 커스터마이징이 가능
- LocalDate, LocalDateTime, LocalTime 설정
  - 주고 받는 데이터 타입으로 LocalDate, LocalDateTime, LocalTime이 사용될 경우 DateTimer와 관련된 Formatter 추가 설정이 필요
  ```java
  @Bean
  public FeignFormatterRegistrar dateTimeFormatterRegistrar() {
      return registry -> {
          DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
          registrar.setUseIsoFormat(true);
          registrar.registerFormatters(registry);
      };
  }
  ```
  - ObjectMapper가 해당 타입들을 Serialize, Deserialize하기 위한 라이브러리가 존재하지 않을 경우에는 아래의 의존성을 추가
  ```
  implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'
  ```
  
### 4. 사용시 주의사항
- URI의 마지막 슬래시(/) 제거
  - 명시된 URI : http://www.test.com/test/
  - 실제 요청 URI : http://www.test.com/test
- OpenFeign의 RequestTemplate 클래스에서 요청 URI가 슬래시(/)로 끝날 경우 해당 부분을 제거하는 로직이 존재
- 따라서 마지막 슬래시가 제거되고 있으므로 필요할 경우 인터셉터 등을 사용해서 별도의 설정을 추가해주어야 함