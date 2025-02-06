package ru.manannikov.filesharingservice.s3.services;

import ru.manannikov.filesharingservice.s3.dto.BucketResponse;

import java.util.List;

public interface BucketService {
    List<BucketResponse> findAll();

    String create(String bucketName);

    String delete(String bucketName);
}
