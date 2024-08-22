package ru.manannikov.filesharingservice.securityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;

@Schema(description = "Ответ на ошибку со стороны клиента или сервера")
public record ExceptionBody(
    @Schema(description = "Краткое описание ошибки")
    String message,
    @Schema(description = "Момент возникновения ошибки")
    String timestamp
) {
    public ExceptionBody(String message) {
        this(
            message,
            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
        );
    }
}
