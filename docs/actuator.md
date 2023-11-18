## Actuator

- 애플리케이션의 상태를 모니터링, Metric 수집을 위한 Endpoint 제공

### Example
- 의존성 추가
  ```groovy
  implementation "org.springframework.boot:spring-boot-starter-actuator"
  ```
- Config
  - 모든 Endpoint 노출
    ```yaml
    management:
    endpoints:
      web:
        exposure:
          include: "*"
    ```
  - 특정 Endpoint 노출
    ```yaml
    management:
    endpoints:
      web:
        exposure:
          include: {Endpoint name}
    ```

### Endpoints
- Endpoint의 기본 prefix는 "/actuator"이며, 변경 가능
  ```yaml
  management:
    endpoints:
      web:
        base-path: /{custom prefix}
  ```
- Default Endpoint
  - [Actuator Endpoints](https://docs.spring.io/spring-boot/docs/3.1.2/actuator-api/htmlsingle/#overview)
- Custom Endpoint
  - 특정한 비즈니스 로직에 대한 모니터링 정보 제공 가능
  - ```/actuator/customEndpoint```을 통해 예시에서 정의한 Endpoint 확인 가능
  - Example
    ```java
    @Component
    @Endpoint(id = "customEndpoint") // 지정된 {id}에 사용자 정의 Endpoint를 생성
    public class CustomEndpoint {
      @ReadOperation // 해당 Endpoint에서 수행할 동작을 정의
      public CustomInfo customInfo() {
          return new CustomInfo("This is a custom endpoint", "v1.0");
      }
  
      public static class CustomInfo { 
        private final String description;
        private final String version;
  
        public CustomInfo(String description, String version) {
            this.description = description;
            this.version = version;
        }
  
        public String getDescription() {
            return description;
        }
  
        public String getVersion() {
            return version;
        }
      }
    }
    ```

### Security
- Spring Security를 사용하여 Actuator Endpoint에 대한 인증 구현
- 의존성 추가
  ```groovy
  implementation "org.springframework.boot:spring-boot-starter-security"
  ```
- Example
  ```java
    @Configuration
      public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
          .authorizeRequests() 
          .antMatchers("/actuator/**").authenticated() // actuator에 기본 인증을 요구 하도록 설정 (사용자 이름과 비밀번호가 필요)
          .anyRequest().permitAll()
          .and()
          .httpBasic();
      }
    }
  ```
  - 사용자 이름 및 비밀번호 설정
    - 기본 사용자 이름은 user, 자동 생성된 비밀번호 사용
      - 자동 생성 비밀번호는 애플리케이션 시작시 출력
    - 사용자 이름 및 비밀번호 변경 가능
      ```yaml
      spring:
      security:
        user:
          name: {Username}
          password: {Password}
      ```
  - Endpoint에 대한 권한 설정
    - 특정 Endpoint에 대한 RBAC(역할기반 접근제어)는 Spring Security에서 구현 가능
    - 권한 설정 예시 코드
      ```java
      @Configuration
      public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .antMatchers("/actuator/health").permitAll()
                    .antMatchers("/actuator/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
                .and()
                .httpBasic();
        }
      }
      ```