package ru.manannikov.filesharingservice.s3.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.io.InputStream;
import java.util.List;

@Service
public class MinioService {
    private final MinioClient s3Client;
    private final String region;

    public MinioService(@Value("${minio.region}") String region, MinioClient s3Client) {
        this.region = region;
        this.s3Client = s3Client;
//        log.info("region = {}", region);
    }

    public Iterable<Result<Item>> getObjects(String bucketName) {
        return s3Client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .build()
        );
    }

    public Iterable<Result<Item>> getObjectsByUsername(String prefix, String bucketName) {
        return s3Client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(false)
                .prefix(prefix)
            .build()
        );
    }

    public void putObject(final MultipartFile file, String objectId, String bucketName) {
        checkBucket(bucketName);

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucketName)
                    // идентификатор сохраняемого объекта
                    .object(objectId)

                    .contentType(file.getContentType())

                    .build());
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при загрузке файла \"" + file.getOriginalFilename() + "\" в объектное хранилище. Невозможно считать содержимое файла");
        }
    }

    public byte[] getObject(String objectId, String bucketName) {
        checkBucket(bucketName);

        try (
                InputStream inputStream = s3Client.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectId)
                                .build()
                )
        ) {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при загрузке объекта \"" + objectId + "\" из объектного хранилища : " + ex);
        }
    }

    public StatObjectResponse getObjectMetadata(String objectId, String bucketName) {
        try {
            return s3Client.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectId)
                .build()
            );
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при получении метаданных объекта \"" + objectId + "\" из объектного хранилища. Вероятно искомый объект не существует : " + ex);
        }
    }

    public void removeObject(String objectId, String bucketName) {
        checkBucket(bucketName);

        try {
            s3Client.removeObject(
                    RemoveObjectArgs.builder()
                            .object(objectId)
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при попытке удалить объект \"" + objectId + "\" из объектного хранилища : " + ex);
        }
    }

    public List<Bucket> getBuckets() {
        try {
            return s3Client.listBuckets();
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при попытке получить список бакетов : " + ex.getLocalizedMessage());
        }
    }

    public void makeBucket(String bucketName) {
        try {
            s3Client.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .region(region)
                            .objectLock(false)
                            .build()
            );
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при создании бакета \"" + bucketName + "\" : " + ex);
        }
    }

    public void removeBucket(String bucketName) {
        try {
            s3Client.removeBucket(
                    RemoveBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при удалении бакета \"" + bucketName + "\" : " + ex);
        }
    }

    private void checkBucket(String bucketName) {
        try {
            boolean isBucketExists = s3Client.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!isBucketExists) {
                makeBucket(bucketName);
            }
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при проверке бакета : " + ex);
        }
    }
}
