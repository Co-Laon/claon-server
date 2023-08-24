package com.claon.monitor;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(adminServer.path("/assets/**")).permitAll()
                        .requestMatchers(adminServer.path("/login")).permitAll()
                        .requestMatchers(
                                "/actuator/**",
                                "/v2/api-docs", "/v3/api-docs", "/swagger-resources/**",
                                "/webjars/**", "/swagger/**", "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage(adminServer.path("/login")).successHandler(successHandler))
                .logout(logout -> logout.logoutUrl(adminServer.path("/logout"))).httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(adminServer.path("/instances"), HttpMethod.POST.toString()),
                                new AntPathRequestMatcher(adminServer.path("/instances/*"), HttpMethod.DELETE.toString()),
                                new AntPathRequestMatcher(adminServer.path("/actuator/**"))
                        ))
                .rememberMe(rememberMe -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

}