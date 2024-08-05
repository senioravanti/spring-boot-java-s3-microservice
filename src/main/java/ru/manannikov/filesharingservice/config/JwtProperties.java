package ru.manannikov.filesharingservice.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.time.Duration;

/**
 * Используем @Validated, чтобы гарантировать что все устанавливаемые в сеттерах значения -> соответствуют указанным ограничениям.
 */
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("app.security.jwt")
public class JwtProperties {
//    private static final Logger LOG = LoggerFactory.getLogger(JwtProperties.class);

    @NotNull
    private SecretKey key;

    @NotNull
    private JWSAlgorithm algorithm;

    @NotNull
    private String issuer;

    @NotNull
    @DurationMin(minutes = 1)
    private Duration expirationTime;

    public void setAlgorithm(String algorithm) {
        this.algorithm = JWSAlgorithm.parse(algorithm);
    }

    public void setKey(String key) {
//        LOG.debug("key = {}", key);

        var jwk = new OctetSequenceKey.Builder(key.getBytes())
            .algorithm(algorithm)
            .build();

        this.key = jwk.toSecretKey();
    }

}
