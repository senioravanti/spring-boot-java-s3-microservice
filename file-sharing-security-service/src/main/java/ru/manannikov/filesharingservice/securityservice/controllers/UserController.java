package ru.manannikov.filesharingservice.securityservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manannikov.filesharingservice.securityservice.dto.UserDto;
import ru.manannikov.filesharingservice.securityservice.enums.Role;
import ru.manannikov.filesharingservice.securityservice.models.UserEntity;
import ru.manannikov.filesharingservice.securityservice.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(
    name = "UserController",
    description = "Обрабатывает CRUD операции с пользователями, указанные операции доступны пользователям с ролью ADMIN")
public class UserController {
    private final UserService service;

    @PostMapping({"", "/"})
    public UserDto create(@Valid @RequestBody UserDto user) {
        return UserDto.fromEntity(
            service.save(user.toEntity())
        );
    }

    @GetMapping({"", "/"})
    public List<UserDto> findAll() {
        return service.findAll().stream().map(UserDto::fromEntity).toList();
    }

    @PutMapping("/{id}")
    public UserDto update(
        @PathVariable("id") Long id,
        @Valid @RequestBody UserDto userDto
    ) {
        UserEntity user = service.findById(id);

        user.setUsername(userDto.email());
        user.setPassword(userDto.password());
        user.setRole(Role.valueOf(userDto.role()));

        return UserDto.fromEntity(service.save(user));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

}
