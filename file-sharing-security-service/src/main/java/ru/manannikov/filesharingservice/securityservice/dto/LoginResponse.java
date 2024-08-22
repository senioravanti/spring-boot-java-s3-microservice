package ru.manannikov.filesharingservice.securityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на успешную авторизацию пользователя с токеном доступа")
public record LoginResponse(
    @Schema(description = "Данные авторизированного пользователя")
    UserDto user,

    @Schema(description = "Токен доступа к защищенному ресурсу с ограниченным сроком действия", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    String accessToken,
    @Schema(description = "Токен обновления, передают сервису авторизации для повторной выдачи токена доступа после истечения срока действия токена доступа")
    String refreshToken
) {
}
