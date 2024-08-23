package ru.manannikov.filesharingservice.s3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.manannikov.filesharingservice.s3.dto.ExceptionBody;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(
        final MethodArgumentNotValidException ex
    ) {
        ExceptionBody exceptionBody = new ExceptionBody("Произошла ошибка в процессе валидации свойств внешней конфигурации");
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingMessage, newMessage) ->
                                existingMessage + " " + newMessage)
                ));
        return exceptionBody;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ExceptionBody handleNoResourceFoundException(final Exception ex) {
        log.debug("stacktrace NOT_FOUND\n{}", (Object[]) ex.getStackTrace());
        return new ExceptionBody(
                "Искомый ресурс не найден. Возможно отсутствует необходимый параметр пути URL-адреса (path variable), или указан некорректный  URL-адрес",
                Map.of(ex.getClass().getName(), ex.getLocalizedMessage())
        );
    }

    @ExceptionHandler(S3ObjectException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionBody handleFileUploadException(final Exception ex) {
        log.debug("stacktrace BAD_REQUEST\n{}", (Object[]) ex.getStackTrace());
        return new ExceptionBody(
            "Произошла ошибка при выполнении операций с объектным хранилищем",
            Map.of(ex.getClass().getName(), ex.getLocalizedMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ExceptionBody handleException(Exception ex) {
        log.debug("stacktrace INTERNAL_SERVER_ERROR\n{}", (Object[]) ex.getStackTrace());
        return new ExceptionBody(
            "Произошла ошибка",
            Map.of(ex.getClass().getName(), ex.getLocalizedMessage())
        );
    }
}
