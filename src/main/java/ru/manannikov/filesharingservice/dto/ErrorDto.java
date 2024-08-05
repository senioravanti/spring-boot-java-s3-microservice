package ru.manannikov.filesharingservice.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public record ErrorDto (
    int errorCode,
    String message,
    String timestamp
) {
    public ErrorDto(int errorCode, String message) {
        this(
            errorCode,
            message,
            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
        );
    }
}
