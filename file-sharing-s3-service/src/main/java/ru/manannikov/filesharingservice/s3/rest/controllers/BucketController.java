package ru.manannikov.filesharingservice.s3.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.manannikov.filesharingservice.s3.dto.BucketResponse;
import ru.manannikov.filesharingservice.s3.rest.api.BucketApi;
import ru.manannikov.filesharingservice.s3.services.impl.BucketServiceImpl;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class BucketController
    implements BucketApi
{
    private final BucketServiceImpl service;

    @Override
    public List<BucketResponse> getAll() {
        List<BucketResponse> buckets = service.findAll();

        buckets.forEach(bucket -> bucket.add(
            linkTo(methodOn(BucketController.class).delete(bucket.getName())).withRel("delete")
        ));

        return buckets;
    }

    @Override
    public ResponseEntity<String> create (
        String bucketName
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(bucketName));
    }

    @Override
    public ResponseEntity<String> delete(
        String bucketName
    ) {
        return ResponseEntity.ok(service.delete(bucketName));
    }
}