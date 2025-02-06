package ru.manannikov.filesharingservice.s3.services.impl;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.dto.ObjectResponseDto;
import ru.manannikov.filesharingservice.s3.exception.NotFoundException;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;
import ru.manannikov.filesharingservice.s3.services.MinioService;
import ru.manannikov.filesharingservice.s3.services.ObjectService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service("objectService")
@RequiredArgsConstructor
public class ObjectServiceImpl
    implements ObjectService
{
    private static final Logger logger = LogManager.getLogger(ObjectServiceImpl.class);

    private final MinioService minioService;

    @Override
    public List<ObjectResponseDto> findByBucketName(
        String bucketName
    ) {
        if (!minioService.isBucketExists(bucketName)) {
            throw new NotFoundException(String.format(
                "Бакет %s не существует", bucketName
            ));
        }

        Iterable<Result<Item>> objects = minioService.listBucket(
            bucketName
        );

        return StreamSupport.stream(objects.spliterator(), true)
            .map(object -> {
                try {
                    String objectName = object.get().objectName();

                    return getMetadata(
                        bucketName,
                        objectName
                    );
                } catch (Exception ex) {
                    logger.error(ex);
                    throw new S3ObjectException(
                        "Ошибка при формировании списка объектов бакета " + bucketName
                    );
                }
            })
            .toList()
        ;
    }

    @Override
    public ByteArrayResource download(
        String bucketName,
        String objectName
    ) {
        minioService.getObjectMetadata(
            bucketName,
            objectName
        );

        final var data = minioService.getObject(
            bucketName,
            objectName
        );
        return new ByteArrayResource(data);
    }
    
    @Override
    public ObjectResponseDto save(
        String bucketName,
        MultipartFile file 
    ) {
        if (!minioService.isBucketExists(bucketName)) {
            throw new NotFoundException(String.format(
                "Бакет %s не существует", bucketName
            ));
        }

        final String originalFileName = file.getOriginalFilename();
        if (file.isEmpty() || originalFileName == null) {
            throw new S3ObjectException("Ошибка при загрузке объекта: у файла должно быть название");
        }

        String objectName = originalFileName.substring(originalFileName.lastIndexOf("/") + 1);

        try {
            minioService.getObjectMetadata(
                bucketName,
                objectName
            );
            throw new S3ObjectException(
                String.format("Объект %s/%s уже загружен в объектное хранилище", bucketName, objectName)
            );
        } catch(NotFoundException ex) {
            minioService.putObject(
                bucketName,
                objectName,

                file
            );

            logger.info("Объект {}/{} успешно загружен в объектное хранилище", bucketName, objectName);
            return ObjectResponseDto.builder()
                .objectName(objectName)

                .size(file.getSize())
                .httpContentType(file.getContentType())

                .lastModifiedDateTime(ZonedDateTime.now())
            .build();
        }
    }

    @Override
    public ObjectResponseDto update(
        String bucketName,
        String objectName,
        
        MultipartFile file
    ) {
        final var objectMetadata = minioService.getObjectMetadata(
            bucketName,
            objectName
        );

        minioService.putObject(
            bucketName,
            objectName,

            file
        );

        logger.info("Новая версия объекта {}/{} успешно загружена", bucketName, objectName);
        return ObjectResponseDto.builder()
            .objectName(objectName)

            .size(objectMetadata.size())
            .httpContentType(objectMetadata.contentType())

            .lastModifiedDateTime(objectMetadata.lastModified())
        .build();
    }

    @Override
    public void delete(
        String bucketName,
        String objectName
    ) {
        minioService.getObjectMetadata(
            bucketName,
            objectName
        );

        minioService.removeObject(
            bucketName,
            objectName
        );
    }

    private ObjectResponseDto getMetadata(
        String bucketName,
        String objectName
    ) {

        StatObjectResponse objectMetadata = minioService.getObjectMetadata(
            bucketName,
            objectName
        );

        return ObjectResponseDto.builder()
            .objectName(objectName)

            .size(objectMetadata.size())
            .httpContentType(objectMetadata.contentType())

            .lastModifiedDateTime(objectMetadata.lastModified())

            .build();
    }
}
