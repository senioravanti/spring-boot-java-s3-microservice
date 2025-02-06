package ru.manannikov.filesharingservice.s3.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.manannikov.filesharingservice.s3.dto.BucketResponse;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import static ru.manannikov.filesharingservice.s3.utils.Constants.INTERNAL_SERVER_ERROR_API_RESPONSE;

@Tag(
    name = "Созданные администратором бакеты",
    description = "Позволяет администратору создавать и удалять бакеты"
)
@RequestMapping("/v1/buckets")
public interface BucketApi {

    @GetMapping({"", "/"})
    @Operation(
        summary = "Вывести список бакетов"
    )
    List<BucketResponse> getAll();

    @PostMapping("/create/{bucketName}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Создать бакет",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Бакет успешно создан",
                content = @Content(
                    mediaType = APPLICATION_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Ошибка при создании бакета, некорректные входные данные",
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_API_RESPONSE,
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            )
        }
    )
    ResponseEntity<String> create(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название нового бакета",
            required = true
        )
        String bucketName
    );

    @DeleteMapping("/delete/{bucketName}")
    @Operation(
        summary = "Удалить бакет",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Бакет успешно удален",
                content = @Content(
                    mediaType = APPLICATION_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Ошибка при удалении бакета, некорректные входные данные",
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_API_RESPONSE,
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            )
        }
    )
    ResponseEntity<String> delete(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название бакета",
            required = true
        )
        String bucketName
    );
}
