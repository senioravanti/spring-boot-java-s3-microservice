package ru.manannikov.filesharingservice.gatewayserver.util;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtUtil {

    private final JwtParser parser;

    public JwtUtil(@Value("${app.security.jwt.key}") String key) {
//        log.debug("key: {}", key);
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());
        parser = Jwts
                .parser()
                .verifyWith(secretKey)
                .build();
    }

    public String getAuthorities(final String token) {
        return parser
            .parseSignedClaims(token)
            .getPayload()
            .get("authorities").toString();
    }

    public String getUsername(final String token) {
        return parser
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
