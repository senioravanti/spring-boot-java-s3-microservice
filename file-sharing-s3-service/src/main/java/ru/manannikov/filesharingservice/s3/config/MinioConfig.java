package ru.manannikov.filesharingservice.s3.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties properties;

    @Bean
    public MinioClient setupS3Client() {
        return MinioClient.builder()
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            // URL-Адрес сервера Minio
            .endpoint(properties.getUrl())

        .build();
    }
}
