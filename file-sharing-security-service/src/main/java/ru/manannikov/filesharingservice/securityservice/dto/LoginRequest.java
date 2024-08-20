package ru.manannikov.filesharingservice.securityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на авторизацию")
public record LoginRequest(

    @NotBlank(message = "Введите адрес электронной почты")
    @Email(message = "Введен некорректный адрес электронной почты")
    @Schema(description = "Адрес электронной почты", example = "example@vk.com")
    String email,

    @NotBlank(message = "Введите пароль")
    @Size(min = 4, max = 255, message = "Длина пароля должна быть от 4 до 255 символов")
    @Schema(description = "Пароль", example = "12345")
    String password
) {
}
