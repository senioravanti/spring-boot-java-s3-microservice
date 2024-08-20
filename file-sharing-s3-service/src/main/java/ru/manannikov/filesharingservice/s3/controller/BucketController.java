package ru.manannikov.filesharingservice.s3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.manannikov.filesharingservice.s3.dto.BucketResponseDto;
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
    private final BucketService service;

    @GetMapping({"", "/"})
    @Operation(
        summary = "Вывести список бакетов"
    )
    public List<BucketResponseDto> getAll() {
        List<BucketResponseDto> buckets = service.listAll();

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