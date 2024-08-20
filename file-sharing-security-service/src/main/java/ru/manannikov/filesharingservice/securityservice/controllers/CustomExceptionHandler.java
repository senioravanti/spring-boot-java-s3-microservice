package ru.manannikov.filesharingservice.securityservice.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.manannikov.filesharingservice.securityservice.dto.ErrorDto;
import ru.manannikov.filesharingservice.securityservice.exceptions.UserNotFoundException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CustomExceptionHandler {
    /**
     * Обработчик кастомной ошибки;
     * @param error -> UserNotFoundException, которую выбрасывает UserSrvice, если искомый пользователь не найден;
     * @return ErrorDto
     */
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorDto handleNotFoundExceptions(final UserNotFoundException error) {
        return new ErrorDto(NOT_FOUND.value(), error.getLocalizedMessage());
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        IllegalArgumentException.class,
        DataIntegrityViolationException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorDto handleBadRequestExceptions(final Exception error) {
        return new ErrorDto(BAD_REQUEST.value(), error.getLocalizedMessage());
    }
    /**
     * Обработчик ошибок, возникающих в ходе аутентификации, таких как: UsernameNotFoundException, BadCredentialsException, InternalAuthenticationServiceException;
     * @param error -> AuthenticationException -> может выбросить DaoAuthenticationProvider, если не найдет пользователя с указанным email;
     * @return json формата ErrorDto сод. статус-код, детальное описание возникшей ошибки и моментом ее возникновения.
     */
    @ExceptionHandler
    @ResponseStatus(UNAUTHORIZED)
    public ErrorDto handleAuthenticationExceptions(final AuthenticationException error) {
        return new ErrorDto(UNAUTHORIZED.value(), "В ходе аутентификации произошла ошибка, в базе данных не найдено соответствие переданным регистрационным данным, см. " + error.getLocalizedMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorDto handleIllegalStateExceptions(final IllegalStateException error) {
        return new ErrorDto(INTERNAL_SERVER_ERROR.value(), "Произошла ошибка на стороне сервера, см. " + error.getLocalizedMessage());
    }
}
