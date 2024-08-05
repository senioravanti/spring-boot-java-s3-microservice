package ru.manannikov.filesharingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.manannikov.filesharingservice.enums.Role;
import ru.manannikov.filesharingservice.models.UserEntity;

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
