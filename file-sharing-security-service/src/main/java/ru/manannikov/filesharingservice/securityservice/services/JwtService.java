package ru.manannikov.filesharingservice.securityservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.JwsHeader;
//import org.springframework.security.oauth2.jwt.JwtClaimsSet;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.manannikov.filesharingservice.securityservice.config.JwtProperties;
import ru.manannikov.filesharingservice.securityservice.dto.LoginResponse;
import ru.manannikov.filesharingservice.securityservice.dto.UserDto;
import ru.manannikov.filesharingservice.securityservice.enums.Role;
import ru.manannikov.filesharingservice.securityservice.models.UserEntity;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Создает токены доступа в формате JWT и отправляет их клиенту
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;
    private final UserService userService;

//    private final JwtEncoder jwtEncoder;
    /**
     * Генерирует токен доступа в формате JWT
     *
     * @return токен доступа в формате jwt.
     */
    public String generateAccessToken(
        final Long id,
        final String username,
        final Role role
    ) {
        // Мне нужен только последний элемент коллекции -> роль.
        // Claims -> объект, который будет хранить в токене информацию о пользователе.
        Instant issuedTime = Instant.now();

        Claims claims = Jwts.claims()
            // sub
            .subject(username)
            // jti
            .id(String.valueOf(id))
            // iat
            .issuedAt(Date.from(issuedTime))
            .add("authorities", role.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toSet()))
        .build();

        return Jwts.builder()
            .claims(claims)
            .expiration(Date.from(issuedTime.plus(jwtProperties.getAccessKeyExpirationTime())))
            .signWith(jwtProperties.getKey())
        .compact();
    }

    public String generateRefreshToken(
        final Long id,
        final String username
    ) {
        Instant issuedTime = Instant.now();

        Claims claims = Jwts.claims()
            .subject(username)
            .id(String.valueOf(id))

            .issuedAt(Date.from(issuedTime))
        .build();

        return Jwts.builder()
            .claims(claims)
            .expiration(Date.from(issuedTime.plus(jwtProperties.getRefreshKeyExpirationTime())))
            .signWith(jwtProperties.getKey())
        .compact();
    }

    public boolean isValid(final String token) {
        Jws<Claims> claims = Jwts.parser()
            .verifyWith(jwtProperties.getKey())
        .build().parseSignedClaims(token);
        return claims.getPayload().getExpiration().after(new Date());
    }

    public UserEntity getUserFromRefreshToken(
        final String refreshToken
    ) {
        if (!isValid(refreshToken)) throw new AccessDeniedException("Refresh token is not valid");

        String jti = Jwts
            .parser()
                .verifyWith(jwtProperties.getKey())
            .build()
            .parseSignedClaims(refreshToken)
                .getPayload()
                .getId();

        Long id = Long.valueOf(jti);
        return userService.findById(id);
    }

//    public Authentication getAuthentication(
//        final String token
//    ) {
//        String username = Jwts
//            .parser()
//                .verifyWith(jwtProperties.getKey())
//            .build()
//            .parseSignedClaims(token)
//                .getPayload()
//            .getSubject()
//        ;
//        UserDetails user = userService.loadUserByUsername(username);
//        return new UsernamePasswordAuthenticationToken(
//            username,
//            user.getPassword(),
//            user.getAuthorities()
//        );
//    }
}
