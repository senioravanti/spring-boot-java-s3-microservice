package ru.manannikov.filesharingservice.s3.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "minio")
@Getter @Setter
@Validated
public class MinioProperties {
    @NotNull
    private String bucket;
    @NotNull
    private String url;
    @NotNull
    private String accessKey;
    @NotNull
    private String secretKey;
}
