package ru.manannikov.filesharingservice.s3.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.dto.ObjectResponseDto;
import ru.manannikov.filesharingservice.s3.rest.api.ObjectApi;
import ru.manannikov.filesharingservice.s3.services.impl.ObjectServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@RestController
@RequiredArgsConstructor
public class ObjectController
    implements ObjectApi
{
    private static final Logger logger = LogManager.getLogger(ObjectController.class);
    private final ObjectServiceImpl service;

    @Override
    public List<ObjectResponseDto> getAll(
        String bucketName
    ) {
        return service.findByBucketName(bucketName);
    }

    @Override
    public ObjectResponseDto createObject(
        String bucketName,

        MultipartFile file
    ) {
        logger.debug("Обработка запроса на загрузку объекта в бакет {}", bucketName);
        ObjectResponseDto object = service.save(
            bucketName,
            file
        );

        object.add(
            linkTo(methodOn(ObjectController.class).download(bucketName, object.getObjectName(), APPLICATION_OCTET_STREAM_VALUE)).withRel("download"),
            linkTo(methodOn(ObjectController.class).delete(bucketName, object.getObjectName())).withRel("delete")
        );

        return object;
    }


    @Override
    public ObjectResponseDto updateObject(
        String bucketName,
        String objectName,

        MultipartFile file
    ) {
        logger.debug("Обработка запроса на загрузку новой версии объекта {}/{}", bucketName, objectName);
        return service.update(
            bucketName, objectName,
            file
        );
    }


    @Override
    public ResponseEntity<Resource> download(
        String bucketName,
        String objectName,

        @Nullable String httpContentType
    ) {
        final var file = service.download(
            bucketName, objectName
        );

        MediaType contentType;
        try {
            contentType = MediaType.valueOf(
                Optional.ofNullable(httpContentType).orElse(APPLICATION_OCTET_STREAM_VALUE)
            );
        } catch (InvalidMediaTypeException ex) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
            .contentLength(file.contentLength())
            .contentType(contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
        .body(file);
    }


    @Override
    public ResponseEntity<Void> delete(
        String bucketName,
        String objectName
    ) {
        logger.debug("Обработка запроса на удаление объекта {}/{}", bucketName, objectName);
        service.delete(
            bucketName, objectName
        );
        return ResponseEntity.ok().build();
    }
}
