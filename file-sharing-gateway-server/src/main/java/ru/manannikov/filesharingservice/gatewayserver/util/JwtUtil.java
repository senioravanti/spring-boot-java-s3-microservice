package ru.manannikov.filesharingservice.gatewayserver.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties properties;

    public void validateToken(final String token) {
        Jwts.parser().verifyWith(properties.getKey()).build().parseSignedClaims(token);
    }

}
