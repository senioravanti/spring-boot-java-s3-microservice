package ru.manannikov.filesharingservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manannikov.filesharingservice.dto.LoginRequest;
import ru.manannikov.filesharingservice.dto.LoginResponse;
import ru.manannikov.filesharingservice.dto.SignUpRequest;
import ru.manannikov.filesharingservice.dto.UserDto;
import ru.manannikov.filesharingservice.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
    name = "AuthController",
    description = "Отвечает за за регистрацию и аутентификацию пользователей, операции \"зарегистрироваться\" и \"войти\" доступны всем пользователям API")
public class AuthController {
    private final AuthService service;

    @PostMapping("/signup")
    @Operation(summary = "Обрабатывает регистрацию пользователей, при регистрации пользователь обязательное передает адрес электронной почты и пароль, а также дополнительно указывает имя и фамилию.")
    public UserDto signup(@Valid @RequestBody SignUpRequest userDto) {
        return UserDto.fromEntity(
            service.signup(userDto.toEntity())
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Обрабатывает аутентификацию пользователей, пользователь передает адрес электронной почты и пароль, в случае успеха получает полную информацию о пользователе с переданными учетными данными и токен доступа в формате JWT, который действует ровно один час.")
    public LoginResponse login(@Valid @RequestBody LoginRequest userData) {
        return service.login(userData.email(), userData.password());
    }
}
