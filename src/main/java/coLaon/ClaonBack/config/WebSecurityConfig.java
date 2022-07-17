package coLaon.ClaonBack.config;

import coLaon.ClaonBack.common.utils.CookieUtil;
import coLaon.ClaonBack.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Value("${spring.jwt.access-token.cookie-name}")
    private String ACCESS_COOKIE_NAME;
    @Value("${spring.jwt.refresh-token.cookie-name}")
    private String REFRESH_COOKIE_NAME;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()
                .csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(this.jwtAuthenticationEntryPoint)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .permitAll()

                .and()
                .cors()
                .configurationSource(this.corsConfigurationSource())

                .and()
                .authorizeRequests()
                .antMatchers(
                        "/api/**/auth/nickname/**/duplicate-check",
                        "/api/**/auth/instagram/account",
                        "/api/**/auth/sign-in/**",
                        "/api/**/posts/**/like",
                        "/api/**/posts/**/comment",
                        "/api/**/posts/comment/**/children"
                )
                .permitAll()
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(new JwtAuthFilter(this.jwtUtil, this.cookieUtil), UsernamePasswordAuthenticationFilter.class)

                .logout()
                .logoutUrl("/api/**/user/sign-out")
                .deleteCookies(this.ACCESS_COOKIE_NAME, this.REFRESH_COOKIE_NAME);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/v2/api-docs", "/v3/api-docs", "/swagger-resources/**",
                "/webjars/**", "/swagger/**", "/swagger-ui/**"
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // TODO: set allowed origin
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
