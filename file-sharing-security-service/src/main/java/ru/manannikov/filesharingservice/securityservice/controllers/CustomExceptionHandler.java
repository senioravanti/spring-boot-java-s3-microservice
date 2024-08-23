package ru.manannikov.filesharingservice.securityservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.manannikov.filesharingservice.securityservice.dto.ExceptionBody;
import ru.manannikov.filesharingservice.securityservice.exceptions.UserNotFoundException;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    /**
     * Обработчик кастомной ошибки;
     * @param ex -> UserNotFoundException, которую выбрасывает UserSrvice, если искомый пользователь не найден;
     * @return ErrorDto
     */
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ExceptionBody handleNotFoundExceptions(final UserNotFoundException ex) {
        return new ExceptionBody("Пользователь с указанными учетными данными не найден");
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        IllegalArgumentException.class,
        DataIntegrityViolationException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ExceptionBody handleBadRequestExceptions(final Exception ex) {
        log.debug("see BadRequestExceptions stack trace: {}", Arrays.toString(ex.getStackTrace()));
        return new ExceptionBody(
            "Указаны некорректные учетные данные при регистрации, или отсутствуют необходимые с-ва конфигурации"
        );
    }
    /**
     * Обработчик ошибок, возникающих в ходе аутентификации, таких как: UsernameNotFoundException, BadCredentialsException, InternalAuthenticationServiceException;
     * @param ex -> AuthenticationException -> может выбросить DaoAuthenticationProvider, если не найдет пользователя с указанным email;
     * @return json формата ErrorDto сод. статус-код, детальное описание возникшей ошибки и моментом ее возникновения.
     */
//    @ExceptionHandler
//    @ResponseStatus(UNAUTHORIZED)
//    public ExceptionBody handleAuthenticationExceptions(final AuthenticationException ex) {
//        log.debug("see AuthenticationExceptions stack trace: {}", Arrays.toString(ex.getStackTrace()));
//        return new ExceptionBody("В ходе аутентификации произошла ошибка, в базе данных не найдено соответствие переданным регистрационным данным");
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(FORBIDDEN)
//    public ExceptionBody handleAccessDeniedExceptions(final AccessDeniedException ex) {
//        log.debug("see AccessDeniedExceptions stack trace: {}", Arrays.toString(ex.getStackTrace()));
//        return new ExceptionBody("У вас нет полномочий для доступа к данному ресурсу");
//    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ExceptionBody handleIllegalStateExceptions(final IllegalStateException ex) {
        log.debug("see IllegalStateExceptions stack trace: {}", Arrays.toString(ex.getStackTrace()));
        return new ExceptionBody("Произошла ошибка на стороне сервера");
    }
}
