package ru.manannikov.filesharingservice.securityservice.config;

//import com.nimbusds.jose.JWSAlgorithm;
//import com.nimbusds.jose.jwk.OctetSequenceKey;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.time.Duration;

/**
 * Используем @Validated, чтобы гарантировать что все устанавливаемые в сеттерах значения -> соответствуют указанным ограничениям.
 */
@Component @Validated
@Getter @Setter
@ConfigurationProperties("app.security.jwt")
public class JwtProperties {
    @NotNull
    private SecretKey key;

    @NotNull
    @DurationMin(minutes = 30)
    private Duration accessKeyExpirationTime;

    @NotNull
    @DurationMin(days = 1)
    private Duration refreshKeyExpirationTime;

    public void setKey(String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

}
