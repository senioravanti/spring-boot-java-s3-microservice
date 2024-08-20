package ru.manannikov.filesharingservice.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Статус код возвращается в заголовке ответа, в тело его помещать не имеет смысла
 */
@Getter @Setter
@AllArgsConstructor
public class ExceptionBody {
    private String message;
    private Map<String, String> errors;

    public ExceptionBody(String message) {
        this.message = message;
    }

}
