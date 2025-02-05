package ru.manannikov.filesharingservice.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception
    {
        http.csrf(
            AbstractHttpConfigurer::disable
        );

        http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.oauth2ResourceServer(
            auth2ResourceServer -> auth2ResourceServer
                .jwt(jwt -> jwt
                    .jwkSetUri(jwkSetUri)
                )
        );

        http.authorizeHttpRequests(
            auth -> auth
                .anyRequest().permitAll()
        );

        return http.build();
    }
}
