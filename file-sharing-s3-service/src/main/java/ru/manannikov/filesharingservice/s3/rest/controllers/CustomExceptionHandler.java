package ru.manannikov.filesharingservice.s3.rest.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.manannikov.filesharingservice.s3.dto.ViolationDto;
import ru.manannikov.filesharingservice.s3.exception.NotFoundException;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LogManager.getLogger(CustomExceptionHandler.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex
    ) {
        logger.error("Validation error:\n{}", ex.toString());

        final var problemDetail = createDefaultProblemDetail(
            BAD_REQUEST,
            "Ошибка валидации",
            ex
        );

        final var bindingResult = ex.getBindingResult();
        final var violations = bindingResult.getFieldErrors().stream()
            .map(violation -> new ViolationDto(
                violation.getField(),
                Optional.ofNullable(violation.getDefaultMessage()).orElse("Сообщение об ошибке отсутствует")
            ))
            .toList()
        ;

        problemDetail.setType(createTagUri(MethodArgumentNotValidException.class.getName()));

        final Map<String, Object> properties = Optional
            .ofNullable(
                problemDetail.getProperties()
            )
            .orElseGet(HashMap::new)
        ;
        properties.put("objectName", bindingResult.getObjectName());
        properties.put("violations", violations);

        return problemDetail;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ProblemDetail handleNotFoundException(
        NotFoundException ex
    ) {
        logger.error("resource not found:\n{}", ex.toString());
        return createDefaultProblemDetail(
            NOT_FOUND,
            "Искомый ресурс не найден",
            ex
        );
    }

    @ExceptionHandler(S3ObjectException.class)
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleS3ObjectException(
        S3ObjectException ex
    ) {
        return createDefaultProblemDetail(
            BAD_REQUEST,
            "Ошибка в ходе выполнении операции с объектным хранилищем, некорректные входные данные",
            ex
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(
        Exception ex
    ) {
        logger.error("unhandled exception:\n{}", ex.toString());

        return createDefaultProblemDetail(
            INTERNAL_SERVER_ERROR,
            "Внутренняя ошибка сервера",
            ex
        );
    }

    private ProblemDetail createDefaultProblemDetail(
        HttpStatus statusCode,
        String description,
        Throwable ex
    ) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(
            statusCode,
            description
        );

        problemDetail.setType(createTagUri(ex.getClass().getName()));

        final Map<String, Object> properties = new HashMap<>();
        properties.put("description", ex.getLocalizedMessage());
        problemDetail.setProperties(properties);

        return problemDetail;
    }

    private static URI createTagUri(String qualifiedClassName)
    {
        final String ssp = String.format(
            "%s,%s",
            qualifiedClassName,
            LocalDateTime.now().format(dateTimeFormatter)
        );
        try {
            return new URI("tag", ssp, null);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
