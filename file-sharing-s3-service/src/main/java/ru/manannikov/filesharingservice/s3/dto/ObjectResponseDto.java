package ru.manannikov.filesharingservice.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "Ответ на успешную загрузку/обновление объекта. Метаданные объекта")
public class ObjectResponseDto extends RepresentationModel<ObjectResponseDto> {
    @Schema(description = "Идентификатор объекта")
    private String objectName;

    @Schema(description = "Размер объекта в байтах")
    private Long size;
    @Schema(description = "MIME тип объекта")
    private String httpContentType;

    private ZonedDateTime lastModifiedDateTime;
}
