package ru.manannikov.filesharingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.manannikov.filesharingservice.enums.Role;
import ru.manannikov.filesharingservice.models.UserEntity;

@Schema(description = "Запрос на регистрацию")
public record SignUpRequest(

        @NotBlank(message = "Адрес электронной почты не может быть пустым")
        @Email(message = "Введен некорректный адрес электронной почты")
        @Schema(description = "Адрес электронной почты", example = "example@vk.com")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 4, max = 255, message = "Длина пароля должна быть от 4 до 255 символов")
        @Schema(description = "Пароль", example = "12345")
        String password,

        @Schema(description = "Имя")
        String firstname,
        @Schema(description = "Фамилия")
        String lastname
) {
        public UserEntity toEntity() {
                return new UserEntity(
                null, email, password, firstname, lastname, Role.USER
                );
        }
}
