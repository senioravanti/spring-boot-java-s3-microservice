package ru.manannikov.filesharingservice.s3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.manannikov.filesharingservice.s3.dto.BucketResponse;
import ru.manannikov.filesharingservice.s3.service.BucketService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/bucket")
@RequiredArgsConstructor
@Tag(
    name = "Созданные администратором бакеты",
    description = "Позволяет администратору создавать и удалять бакеты"
)
public class BucketController {
    private static final Logger logger = LogManager.getLogger(BucketController.class);

    private final BucketService service;

    @GetMapping({"", "/"})
    @Operation(
        summary = "Вывести список бакетов"
    )
    public List<BucketResponse> getAll() {
        logger.info("Запрос на получение списка всех бакетов объектного хранилища");

        List<BucketResponse> buckets = service.listAll();

        buckets.forEach(bucket -> {
            bucket.add(
                linkTo(methodOn(BucketController.class).delete(bucket.getName())).withRel("delete")
            );
        });

        return buckets;
    }

    @PostMapping("/create/{bucketName}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Создать бакет"
    )
    public ResponseEntity<String> create (
        @PathVariable(name = "bucketName") String bucketName
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(bucketName));
    }

    @DeleteMapping("/delete/{bucketName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Удалить бакет"
    )
    public ResponseEntity<String> delete(
        @PathVariable(name = "bucketName") String bucketName
    ) {
        return ResponseEntity.ok(service.delete(bucketName));
    }
}