package ru.manannikov.filesharingservice.s3.service;

import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.manannikov.filesharingservice.s3.dto.BucketResponseDto;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {
    private final MinioService minioService;

    public List<BucketResponseDto> listAll() {
        List<Bucket> buckets = minioService.getBuckets();
        List<Long> objectsCount = new ArrayList<>();
        List<Long> bucketSizes = new ArrayList<>();

        int bucketCount = buckets.size();

        try {
            for (Bucket bucket : buckets) {
                Iterable<Result<Item>> objects = minioService.getObjects(bucket.name());

                long counter = 0;
                long bucketSize = 0;

                for (Result<Item> object : objects) {
                    Item item = object.get();

                    if (item.isDir()) {
                        Iterable<Result<Item>> userObjects = minioService.getObjectsByUsername(item.objectName(), bucket.name());
                        for (Result<Item> userObject : userObjects) {
                            Item userItem = userObject.get();
                            bucketSize += userItem.size();
                            ++counter;
                        }
                    }
                    log.debug("itemName = {}", item.objectName());

                }

                objectsCount.add(counter);
                bucketSizes.add(bucketSize);
            }
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при формировании списка бакетов : " + ex.getLocalizedMessage());
        }

        List<BucketResponseDto> response = new ArrayList<>();
        for (int i = 0; i < bucketCount; ++i) {
            Bucket bucket = buckets.get(i);
            response.add(new BucketResponseDto(
                bucket.name(),
                objectsCount.get(i),
                bucketSizes.get(i),
                bucket.creationDate()
            ));
        }

        return response;
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
