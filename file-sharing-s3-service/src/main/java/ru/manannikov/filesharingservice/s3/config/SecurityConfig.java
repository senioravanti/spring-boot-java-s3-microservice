package ru.manannikov.filesharingservice.s3.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        final var allowedOriginsList = Arrays.asList(allowedOrigins);
        logger.debug("allowed origins: {}", allowedOriginsList);

        final var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(allowedOriginsList);
        corsConfiguration.setAllowedMethods(List.of("*"));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception
    {
        http.csrf(
            AbstractHttpConfigurer::disable
        );

        http.cors(cors -> cors
            .configurationSource(corsConfigurationSource())
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
