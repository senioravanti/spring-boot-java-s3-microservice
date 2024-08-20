package ru.manannikov.filesharingservice.s3.dto;

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
public class ObjectResponseDto extends RepresentationModel<ObjectResponseDto> {
    private String username;
    private String objectName;

    private String bucketName;

    private Long size;
    private String httpContentType;

    private ZonedDateTime lastModifiedDateTime;
}
