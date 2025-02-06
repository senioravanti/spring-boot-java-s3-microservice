package ru.manannikov.filesharingservice.s3.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.dto.ObjectResponseDto;

import java.util.List;

import static org.springframework.http.MediaType.*;
import static ru.manannikov.filesharingservice.s3.utils.Constants.INTERNAL_SERVER_ERROR_API_RESPONSE;
import static ru.manannikov.filesharingservice.s3.utils.Constants.NOT_FOUND_API_RESPONSE;

@RequestMapping("/v1/objects")
@Tag(
    name = "Загруженные пользователем файлы",
    description = "Позволяет пользователю управлять загруженными файлами с помощью CRUD операций"
)
public interface ObjectApi {

    
    @GetMapping({"{bucketName}", "{bucketName}/"})
    @Operation(
        summary = "Вывести метаданные загруженных в бакет файлов"
    )
    List<ObjectResponseDto> getAll(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название бакета",
            required = true
        )    
        String bucketName
    );

    
    @PostMapping(path = {"{bucketName}", "{bucketName}/"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Загрузить файл в объектное хранилище",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Файл успешно загружен в объектное хранилище",
                content = @Content(
                    mediaType = APPLICATION_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Ошибка при загрузке файла в объектное хранилище, файл с таким именем уже загружен в объектное хранилище, некорректные входные данные",
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Целевой бакет не существует",
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
    ObjectResponseDto createObject(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название бакета",
            required = true
        )
        String bucketName,

        @RequestParam(name = "file")
        @Parameter(
            description = "Файл для загрузки",
            required = true
        )
        MultipartFile file
    );

    
    @PutMapping("/{bucketName}/{objectName}")
    @Operation(
        summary = "Обновить содержимое файла",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Новая версия файла успешно загружена в объектное хранилище",
                content = @Content(
                    mediaType = APPLICATION_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Ошибка при загрузке новой версии файла в объектное хранилище, некорректные входные данные",
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = NOT_FOUND_API_RESPONSE,
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
    ObjectResponseDto updateObject(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название бакета",
            required = true
        )
        String bucketName,

        @PathVariable(name = "objectName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Идентификатор объекта",
            required = true
        )
        String objectName,

        @RequestParam(name = "file")
        @Parameter(
            description = "Новое содержимое файла",
            required = true
        )
        MultipartFile file
    );


    @GetMapping("/{bucketName}/{objectName}")
    @Operation(
        summary = "Загрузить файл из объектного хранилища",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Файл успешно загружен из объектного хранилища",
                content = @Content(
                    mediaType = APPLICATION_OCTET_STREAM_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Некорректные входные данные",
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = NOT_FOUND_API_RESPONSE,
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
    ResponseEntity<Resource> download(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название бакета",
            required = true
        )
        String bucketName,

        @PathVariable(name = "objectName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Идентификатор объекта",
            required = true
        )
        String objectName,

        @RequestParam(name = "content_type", required = false)
        @Parameter(
            in = ParameterIn.QUERY,
            name = "httpContentType",
            description = "MIME-тип файла"
        )
        String httpContentType
    );


    @DeleteMapping("/{bucketName}/{objectName}")
    @Operation(
        summary = "Удалить объект",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Объект успешно удален"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Некорректные входные данные",
                content = @Content(
                    mediaType = APPLICATION_PROBLEM_JSON_VALUE
                )

            ),
            @ApiResponse(
                responseCode = "404",
                description = NOT_FOUND_API_RESPONSE,
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
    ResponseEntity<Void> delete(
        @PathVariable(name = "bucketName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Название бакета",
            required = true
        )
        String bucketName,

        @PathVariable(name = "objectName")
        @Parameter(
            in = ParameterIn.PATH,
            description = "Идентификатор объекта",
            required = true
        )
        String objectName
    );
}
