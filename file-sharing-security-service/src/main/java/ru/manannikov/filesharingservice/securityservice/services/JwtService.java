package ru.manannikov.filesharingservice.securityservice.services;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import ru.manannikov.filesharingservice.securityservice.config.JwtProperties;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Создает токены доступа в формате JWT и отправляет их клиенту
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;

    private final JwtEncoder jwtEncoder;
    /**
     * Генерирует токен доступа в формате JWT
     * @param auth -> учетные данные пользователя (адрес эл. почты, пароль и полномочия), прошедшего аутентификацию.
     * @return токен доступа в формате jwt.
     */
    public String generateJwt(Authentication auth) {
        // Мне нужен только последний элемент коллекции -> роль.
        String authorities = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        Instant instant = Instant.now();

        // Заголовок надо ставить явно, потому что по умолчанию используется RS256
        JwsHeader header = JwsHeader.with(MacAlgorithm.from(jwtProperties.getAlgorithm().getName())).build();

        // Формируем полезную нагрузку токена
        JwtClaimsSet claims = JwtClaimsSet.builder()
            // * Стандартные утверждения о клиенте (claim)
            // Приложение, которое выдает токен;
            .issuer(jwtProperties.getIssuer())
            // Момент выдачи токена в формате unix time;
            .issuedAt(instant)
            .expiresAt(instant.plus(jwtProperties.getExpirationTime()))
            // email зарегистрированного пользователя;
            .subject(auth.getName())
            // * Пользовательские утверждения о клиенте (claim)
            .claim("authorities", authorities)
        .build();

        // Получаем jwt, подписывая заголовок и полезную нагрузку выбранным алгоритмом хеширования.
        // JwtEncoderParameters -> заголовок и claim set i.e. полезная нагрузка JWT, которые необходимо подписать
        // JwtEncoder возвращает Jwt, мы его преобразуем в строку.
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
