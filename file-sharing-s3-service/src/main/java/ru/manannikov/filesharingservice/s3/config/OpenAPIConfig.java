package ru.manannikov.filesharingservice.s3.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
            title = "s3service",
            description = "Сервис для работы с s3 совместимым объектным хранилищем Minio проекта filesharingservice",
            version = "1.0"
    )
)
public class OpenAPIConfig {
}
