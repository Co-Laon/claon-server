## Spring Boot Admin

Actuator가 제공하는 애플리케이션 정보를 보기 좋고 접근하기 쉬운 방식으로 시각화하는 모니터링 도구
- 장점
  - <span style="color: green">**Centralized Monitoring**</span>
    - Health, Metrics, Log 등을 보기 좋게 시각화
  - <span style="color: green">**Simplified Management**</span>
    - 애플리케이션을 시작, 정지, 재시작해서 Lifecycle 컨트롤
  - <span style="color: green">**Built-in Security**</span>
    - Spring Security와 쉽게 통합 가능

### Example
- 의존성 추가
  ```groovy
  implementation 'de.codecentric:spring-boot-admin-starter-server:3.1.0'
  ```
- Spring Boot Admin Server 활성화
  ```java
  @SpringBootApplication
  @EnableAdminServer
  public class MonitorApplication {}
  ```

- Spring Security Config
  ```java
  @Configuration
  @RequiredArgsConstructor
  public class WebSecurityConfig {
      private final AdminServerProperties adminServer;
  
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
          successHandler.setTargetUrlParameter("redirectTo");
          successHandler.setDefaultTargetUrl(adminServer.path("/"));
  
          http
                  .authorizeHttpRequests(authorizeRequests -> authorizeRequests // "/login", "actuator/**" 등등 설정된 리소스는 누구나 접근을 허용
                          .requestMatchers(adminServer.path("/assets/**")).permitAll()
                          .requestMatchers(adminServer.path("/login")).permitAll()
                          .requestMatchers(
                                  "/actuator/**",
                                  "/v2/api-docs", "/v3/api-docs", "/swagger-resources/**",
                                  "/webjars/**", "/swagger/**", "/swagger-ui/**"
                          ).permitAll()
                          .anyRequest().authenticated()) // 이 외의 리소스는 접근 권한이 필요하다는 의미
                  .formLogin(formLogin -> formLogin
                          .loginPage(adminServer.path("/login")).successHandler(successHandler)) // login URL 및 Success Handler를 설정
                  .logout(logout -> logout.logoutUrl(adminServer.path("/logout"))).httpBasic(Customizer.withDefaults()) // logout 설정
                  .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 쿠키를 이용하여 CSRF를 보호
                          .ignoringRequestMatchers( // CSRF 비활성화 URL
                                  new AntPathRequestMatcher(adminServer.path("/instances"), HttpMethod.POST.toString()),
                                  new AntPathRequestMatcher(adminServer.path("/instances/*"), HttpMethod.DELETE.toString()),
                                  new AntPathRequestMatcher(adminServer.path("/actuator/**"))
                          ))
                  .rememberMe(rememberMe -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600))
                  .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
  
          return http.build();
      }
  }
  ```

- Notification
  - 대표적으로 Slack, Telegram, Microsoft Teams와 연동 가능
  - [Notification Example](https://docs.spring-boot-admin.com/current/server-notifications.html)

### Config

- [Spring Boot Admin Server Config](https://docs.spring-boot-admin.com/current/server.html)
- Actuator
   ```yml
   management:
     endpoints:
       web:
         exposure:
           include: "*"
           // include: env, health
     endpoint:
       health:
         show-details: always
  ```
- Log
  - poll-timer
    - 로그 갱신 주기
    - default = 1000ms
    ```yml
    boot:
      admin:
        ui:
          poll-timer:
            logfile: 10000 // 10초로 설정
    ```
  - 애플리케이션 로그 파일 추가
    ```yml
    logging:
      file:
        name: center/logs/info.log
  
    management:
      endpoint:
        logfile:
          external-file: ${logging.file.name}
    ```
