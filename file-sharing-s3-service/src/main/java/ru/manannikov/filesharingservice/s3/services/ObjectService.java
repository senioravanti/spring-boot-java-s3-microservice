package ru.manannikov.filesharingservice.s3.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.dto.ObjectResponseDto;

import java.util.List;

public interface ObjectService {
    List<ObjectResponseDto> findByBucketName(
        String bucketName
    );

    ByteArrayResource download(
        String bucketName,
        String objectName
    );

    ObjectResponseDto save(
        String bucketName,
        MultipartFile file
    );

    ObjectResponseDto update(
        String bucketName,
        String objectName,

        MultipartFile file
    );

    void delete(
        String bucketName,
        String objectName
    );
}
