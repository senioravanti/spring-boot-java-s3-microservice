package ru.manannikov.filesharingservice.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BucketResponseDto extends RepresentationModel<BucketResponseDto> {
    private String name;
    private Long objectsCount;
    private Long bucketSize;
    private ZonedDateTime creationDate;
}
