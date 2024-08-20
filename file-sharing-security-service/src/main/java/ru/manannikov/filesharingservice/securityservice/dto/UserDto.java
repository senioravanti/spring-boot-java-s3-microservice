package ru.manannikov.filesharingservice.securityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.manannikov.filesharingservice.securityservice.enums.Role;
import ru.manannikov.filesharingservice.securityservice.models.UserEntity;

@Schema(description = "Данные пользователя")
public record UserDto(
        Long id,

        String email,
        String password,

        String firstname,
        String lastname,

        String role
) {
    public UserEntity toEntity() {
        return new UserEntity(
            id,
            email,
            password,

            firstname,
            lastname,

            Role.valueOf(role)
        );
    }

    public static UserDto fromEntity(UserEntity user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getPassword(),

            user.getFirstname(),
            user.getLastname(),

            user.getRole().name()
        );
    }
}
