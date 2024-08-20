package ru.manannikov.filesharingservice.s3.service;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.config.MinioProperties;
import ru.manannikov.filesharingservice.s3.dto.ObjectResponseDto;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final MinioService minioService;
    private final MinioProperties properties;

    public ObjectResponseDto save(String username, final MultipartFile file, @Nullable String bucketName) {
        String originalFileName = file.getOriginalFilename();
        if (file.isEmpty() || originalFileName == null) {
            throw new S3ObjectException("Ошибка при создании файла : у файла должно быть название");
        }
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String objectName = UUID.randomUUID() + "." + fileExtension;
        String actualBucketName = checkBucketName(bucketName);

        ZonedDateTime createdDateTime = ZonedDateTime.now();
        minioService.putObject(
            file,
            makeObjectId(username, objectName),
            actualBucketName
        );

        return ObjectResponseDto.builder()
            .username(username)
            .objectName(objectName)
            .bucketName(actualBucketName)

            .size(file.getSize())
            .httpContentType(file.getContentType())

            .lastModifiedDateTime(createdDateTime)

            .build();
    }

    public ObjectResponseDto update(String username, String objectName, final MultipartFile file, @Nullable String bucketName) {
        String objectId = makeObjectId(username, objectName);
        String actualBucketName = checkBucketName(bucketName);

        StatObjectResponse objectMetadata = minioService.getObjectMetadata(objectId, actualBucketName);
        log.debug("objectMetadata: {}", objectMetadata);

        Map<String, String> userMetadata = objectMetadata.userMetadata();

        minioService.putObject(
            file,
            objectId,
            actualBucketName
        );

        return ObjectResponseDto.builder()
            .username(username)
            .objectName(objectName)
            .bucketName(actualBucketName)

            .size(objectMetadata.size())
            .httpContentType(objectMetadata.contentType())

            .lastModifiedDateTime(objectMetadata.lastModified())

            .build();
    }

    public ByteArrayResource get(String username, String objectName, @Nullable String bucketName) {
        byte[] data = minioService.getObject(makeObjectId(username, objectName), checkBucketName(bucketName));
        return new ByteArrayResource(data);
    }

    public ObjectResponseDto getMetadata(String username, String objectName, @Nullable String bucketName) {
        String actualBucketName = checkBucketName(bucketName);

        StatObjectResponse objectMetadata = minioService.getObjectMetadata(
            makeObjectId(username, objectName),
            actualBucketName
        );

        return ObjectResponseDto.builder()
            .username(username)
            .objectName(objectName)
            .bucketName(bucketName)

            .size(objectMetadata.size())
            .httpContentType(objectMetadata.contentType())

            .lastModifiedDateTime(objectMetadata.lastModified())

            .build();
    }

    public List<ObjectResponseDto> list(String username, @Nullable String bucketName) {
        String actualBucketName = checkBucketName(bucketName);
        Iterable<Result<Item>> objects = minioService.getObjectsByUsername(username + "/", actualBucketName);

        return StreamSupport.stream(objects.spliterator(), true)
            .map(object -> {
                try {
                    Item item = object.get();
                    String objectId = item.objectName();

                    ObjectResponseDto objectResponseDto = getMetadata(
                            objectId.substring(0, objectId.indexOf("/")),

                            objectId.substring(objectId.indexOf("/") + 1),

                            actualBucketName
                    );

                    return objectResponseDto;
                } catch (Exception ex) {
                    throw new S3ObjectException("Ошибка при формировании списка объектов : " + ex);
                }
            })
        .toList();

    }

    public String delete(String username, String objectName, @Nullable String bucketName) {
        String actualBucketName = checkBucketName(bucketName);
        minioService.removeObject(makeObjectId(username, objectName), actualBucketName);

        return "Объект \"" + objectName + "\", загруженный пользователем \"" + username + "\" успешно удален из бакета \"" + actualBucketName + "\"";
    }
    
    private String checkBucketName(String bucketName) {
        return (bucketName == null || bucketName.isEmpty()) ? 
                properties.getBucket() : 
                bucketName;
    }
    
    private String makeObjectId(String username, String objectName) {
        return username + "/" + objectName;
    }
}
