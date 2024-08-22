package ru.manannikov.filesharingservice.gatewayserver.util;

import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;

@Component @Validated
@Getter @Setter
@ConfigurationProperties("app.security.jwt")
public class JwtProperties {
    @NotNull
    private SecretKey key;

    public void setKey(String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }
}
