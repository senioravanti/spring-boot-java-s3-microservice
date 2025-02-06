package ru.manannikov.filesharingservice.s3.services;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.exception.NotFoundException;
import ru.manannikov.filesharingservice.s3.exception.S3ObjectException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class MinioService {
    private static final Logger logger = LogManager.getLogger(MinioService.class);

    private final MinioClient s3Client;

    public MinioService(MinioClient s3Client) {
        this.s3Client = s3Client;
    }

    public Iterable<Result<Item>> getObjects(String bucketName) {
        return s3Client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .build()
        );
    }

    public Iterable<Result<Item>> listBucket(String bucketName) {
        return s3Client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(false)
            .build()
        );
    }

    public void putObject(
        String bucketName,
        String objectId,

        MultipartFile file
    ) {
        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucketName)
                    // идентификатор сохраняемого объекта
                    .object(objectId)

                    .contentType(file.getContentType())

                    .build());
        } catch (Exception ex) {
            logger.error(ex);
            throw new S3ObjectException("Ошибка при загрузке файла \"" + file.getOriginalFilename() + "\" в объектное хранилище. Невозможно считать содержимое файла");
        }
    }

    public byte[] getObject(
        String bucketName,
        String objectName
    ) {
        try (
            InputStream inputStream = s3Client.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                .build()
            )
        ) {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception ex) {
            logger.error("can't download an object:\n{}", ex.toString());
            throw new S3ObjectException("Ошибка при загрузке объекта \"" + objectName + "\" из объектного хранилища");
        }
    }

    public StatObjectResponse getObjectMetadata(
        String bucketName,
        String objectName
    ) {
        try {
            return s3Client.statObject(
                StatObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (ErrorResponseException ex) {
            throw new NotFoundException(String.format("Объект %s/%s не существует", bucketName, objectName));
        } catch (Exception ex) {
            logger.error("can't get an object metadata:\n{}", ex.toString());
            throw new S3ObjectException("Ошибка при получении метаданных объекта \"" + objectName + "\" из объектного хранилища");
        }
    }

    public void removeObject(
        String bucketName,
        String objectName
    ) {
        try {
            s3Client.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                .build()
            );
        } catch (Exception ex) {
            logger.error("can't delete an object:\n{}", ex.toString());
            throw new S3ObjectException("Ошибка при попытке удалить объект \"" + objectName + "\" из объектного хранилища : " + ex);
        }
    }

    public List<Bucket> getBuckets() {
        try {
            return s3Client.listBuckets();
        } catch (Exception ex) {
            throw new S3ObjectException("Ошибка при попытке получить список бакетов: " + ex.getLocalizedMessage());
        }
    }

    public void makeBucket(String bucketName) {
        try {
            s3Client.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .objectLock(false)
                            .build()
            );
        } catch (Exception ex) {
            logger.error("can't delete a bucket:\n{}", ex.toString());
            throw new S3ObjectException("Ошибка при создании бакета \"" + bucketName + "\"");
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
            logger.error("can't create a bucket:\n{}",  ex.toString());
            throw new S3ObjectException("Ошибка при удалении бакета \"" + bucketName + "\"");
        }
    }

    public boolean isBucketExists(String bucketName)
    {
        try {
            return s3Client.bucketExists(
                BucketExistsArgs
                    .builder()
                    .bucket(bucketName)
                    .build()
            );
        } catch (Exception ex) {
            logger.error("can't check the existence of a bucket:\n{}", ex.toString());
            throw new S3ObjectException("Ошибка при проверке бакета");
        }
    }
}
