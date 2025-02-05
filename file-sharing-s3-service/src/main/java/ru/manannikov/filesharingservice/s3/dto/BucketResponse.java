package ru.manannikov.filesharingservice.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "Метаданные бакета")
public class BucketResponse extends RepresentationModel<BucketResponse> {
    @Schema(description = "Идентификатор бакета")
    private String name;

    @Schema(description = "Количество объектов")
    private Long objectsCount;
    @Schema(description = "Сумма размеров объектов в байтах")
    private Long bucketSize;

    @Schema(description = "Дата и время создания бакета")
    private ZonedDateTime creationDate;
}
