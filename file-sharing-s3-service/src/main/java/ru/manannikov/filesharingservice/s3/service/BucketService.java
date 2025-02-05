package ru.manannikov.filesharingservice.s3.service;

import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.manannikov.filesharingservice.s3.dto.BucketResponse;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final MinioService minioService;

    private static final Logger logger = LogManager.getLogger(BucketService.class);

    private BucketResponse bucketToBucketResponseDto(Bucket bucket) {
        final String bucketName = bucket.name();
        final var bucketObjects = minioService.getObjects(bucketName);

        long objectsCount = StreamSupport.stream(
                bucketObjects.spliterator(),
                false
            )
            .count()
        ;

        long bucketSize = StreamSupport.stream(
                bucketObjects.spliterator(),
                false
            )
            .map(bucketObject -> {
                try {
                    Item bucketItem = bucketObject.get();
                    return bucketItem.size();
                } catch (Exception ex) {
                    throw new S3ObjectException("Ошибка при формировании списка бакетов: " + ex.getLocalizedMessage());
                }
            })
            .reduce(0L, Long::sum)
            ;

        return BucketResponse.builder()
            .name(bucketName)

            .objectsCount(objectsCount)
            .bucketSize(bucketSize)

            .creationDate(bucket.creationDate())
        .build();
    }

    public List<BucketResponse> listAll() {
        List<Bucket> buckets = minioService.getBuckets();

        return buckets.stream()
            .map(this::bucketToBucketResponseDto)
            .toList()
        ;
    }

    public String create(String bucketName) {
        if (bucketName.isBlank()) {
            throw new S3ObjectException("Ошибка при создании бакета : у бакета должно быть название");
        }
        minioService.makeBucket(bucketName);

        return "Бакет \"" + bucketName + "\" успешно создан";
    }

    public String delete(String bucketName) {
        if (bucketName.isBlank()) {
            throw new S3ObjectException("Ошибка при удалении бакета : у бакета должно быть название");
        }
        minioService.removeBucket(bucketName);

        return "Бакет \"" + bucketName + "\" успешно удален";
    }
}
