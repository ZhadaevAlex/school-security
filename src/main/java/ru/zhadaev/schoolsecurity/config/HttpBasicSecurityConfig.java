package ru.zhadaev.schoolsecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class HttpBasicSecurityConfig {

    @Value("${spring.security.remember-me.tokenValiditySeconds}")
    private Integer tokenValiditySeconds;

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(64000);
        filter.setIncludeHeaders(false);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        filter.setIncludeHeaders(true);
        return filter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.level", name = "ru.zhadaev.schoolsecurity", havingValue = "debug")
    public HttpRequestHeadersLoggingFilter getFilter(HttpSecurity httpSecurity) {
        HttpRequestHeadersLoggingFilter filter = new HttpRequestHeadersLoggingFilter();
        httpSecurity.addFilterBefore(filter, SecurityContextPersistenceFilter.class);
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .authorizeHttpRequests()
                .antMatchers("/api-docs/**", "/swagger-ui/**", "/openapi-custom.yaml")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .rememberMe()
                .key("super-secret-key")
                .alwaysRemember(true)
                .tokenValiditySeconds(tokenValiditySeconds);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
