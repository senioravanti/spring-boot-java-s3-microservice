package ru.manannikov.filesharingservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Сервис для обмена файлами между хостом и сервером",
        description = "Для аутентификации используется токен доступа в формате JWT, в claim set токена хранится id пользователя, claim set и заголовок токена подписываются алгоритмом HS256",
        contact = @Contact(
            name = "senioravanti",
            email = "senioravanti@vk.com",
            url = "https://github.com/Antonio-Stradiavanti"
        )
    ),
    servers = {
        @Server(
            description = "Сервис авторизации",
            url = "http://localhost:8001"
        )
    },
    // Делаем указанную security scheme общей для всех контроллеров, в противном случае надо указывать свою @SecurityScheme с помощью @SecurityRequirement отдельно для каждого из контроллеров.
    security = {
        @SecurityRequirement(
            name = "bearerAuth"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
