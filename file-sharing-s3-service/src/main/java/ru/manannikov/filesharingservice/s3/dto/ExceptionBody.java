package ru.manannikov.filesharingservice.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Статус код возвращается в заголовке ответа, в тело его помещать не имеет смысла
 */
@Getter @Setter
@AllArgsConstructor
@Schema(description = "Ответ на ошибку со стороны клиента или сервера")
public class ExceptionBody {
    @Schema(description = "Краткое описание ошибки")
    private String message;
    @Schema(description = "Ассоциативный массив возникших исключений, ключ: полное имя класса-исключения, значение: сообщение, описывающее причину возникновения исключения")
    private Map<String, String> errors;

    public ExceptionBody(String message) {
        this.message = message;
    }

}
