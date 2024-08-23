package ru.manannikov.filesharingservice.s3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.manannikov.filesharingservice.s3.dto.ExceptionBody;
import ru.manannikov.filesharingservice.s3.dto.ObjectResponseDto;
import ru.manannikov.filesharingservice.s3.service.ObjectService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v1/object")
@RequiredArgsConstructor
@Tag(
    name = "Загруженные пользователем файлы",
    description = "Позволяет пользователю управлять загруженными файлами с помощью CRUD операций"
)
public class ObjectController {
    private final ObjectService service;

    @GetMapping({"{username}", "{username}/"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Вывести метаданные загруженных пользователем файлов"
    )
    public List<ObjectResponseDto> getAll(
        @PathVariable(name = "username") String username,

        @RequestParam(name = "bucket_name", required = false) String bucketName
    ) {
        return service.list(username, bucketName);
    }


    @PostMapping(path = {"{username}", "{username}/"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Загрузить файл в объектное хранилище",
        parameters = {
            @Parameter(name = "username", description = "Имя пользователя (без символа \"/\"), которому будет принадлежать загруженный объект, которому принадлежит загружаемый объект", required = true),
            @Parameter(name = "file", description = "Загружаемый массив байт", required = true),

            @Parameter(name = "bucketName", description = "Название бакета. Если не задано, то сервер сохранит файл в бакет, указанный в свойствах внешней конфигурации")
        },
        responses = {
            @ApiResponse(
                description = "Объект успешно создан. Массив байт успешно загружен в объектное хранилище",
                responseCode = "201",
                content = {@Content(
                        mediaType = "application/json"
                )}
            ),
            @ApiResponse(
                description = "Если указанные бакет или пользователь не существуют, или произошла ошибка в ходе загрузки файла в объектное хранилище",
                responseCode = "400",
                content = {@Content(
                            mediaType = "application/json"
                        )}
            ),
            @ApiResponse(
                description = "В случае если в URL адресе не указано имя пользователя",
                responseCode = "404",
                content = {@Content(
                            mediaType = "application/json"
                        )}
            ),
            @ApiResponse(
                description = "Если на сервере в ходе обработки запроса возникло необработанное исключение",
                responseCode = "500",
                content = {@Content(
                            mediaType = "application/json"
                        )}
            )
        }
    )
    public ObjectResponseDto upload (
        @PathVariable(name = "username") String username,

        @RequestPart(name = "file") MultipartFile file,
        @RequestParam(name = "bucket_name", required = false) String bucketName
    ) {
        ObjectResponseDto object = service.save(username, file, bucketName);

        object.add(
            linkTo(methodOn(ObjectController.class).download(username, object.getObjectName(), null, object.getBucketName())).withRel("download"),

            linkTo(methodOn(ObjectController.class).update(username, object.getObjectName(), null, object.getBucketName())).withRel("update"),

            linkTo(methodOn(ObjectController.class).delete(username, object.getObjectName(), object.getBucketName())).withRel("delete")
        );

        return object;
    }


    @PutMapping("/{username}/{objectName}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Обновить содержимое файла",
        parameters = {
                @Parameter(name = "username", description = "Имя пользователя, которому принадлежит загруженный объект", required = true),
                @Parameter(name = "file", description = "Загружаемый массив байт", required = true),
                @Parameter(name = "objectName", description = "Идентификатор объекта в формате \"UUID.*\"", required = true),
                @Parameter(name = "bucketName", description = "Название бакета. Если не задано, то сервер сохранит файл в бакет, указанный в свойствах внешней конфигурации")
        },
        responses = {
                @ApiResponse(
                        description = "Объект успешно обновлен. Массив байт успешно загружен в объектное хранилище. ",
                        responseCode = "201",
                        content = {@Content(
                            mediaType = "application/json"
                        )}
                ),
                @ApiResponse(
                        description = "Если указанные бакет, пользователь или объект не существуют, или произошла ошибка при загрузке массива байт в объектное хранилище",
                        responseCode = "400",
                        content = {@Content(
                            mediaType = "application/json"
                        )}

                ),
                @ApiResponse(
                        description = "В случае если в URL адресе не указаны имя пользователя и/или идентификатор объекта",
                        responseCode = "404",
                        content = {@Content(
                            mediaType = "application/json"
                        )}
                ),
                @ApiResponse(
                        description = "Если на сервере в ходе обработки запроса возникло необработанное исключение",
                        responseCode = "500",
                        content = {@Content(
                            mediaType = "application/json"
                        )}
                )
        }
    )
    public ObjectResponseDto update(
        @PathVariable(name = "username") String username,
        @PathVariable(name = "objectName") String objectName,

        @RequestPart(name = "file") MultipartFile file,

        @RequestParam(name = "bucket_name", required = false) String bucketName
    ) {
        return service.update(username, objectName, file, bucketName);
    }


    @GetMapping("/{username}/{objectName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Загрузить содержимое файла из объектного хранилища",
        parameters = {
                @Parameter(name = "username", description = "Имя пользователя (без символа \"/\"), которому принадлежит загруженный объект.", required = true),
                @Parameter(name = "objectName", description = "Идентификатор объекта в формате \"UUID.*\"", required = true),

                @Parameter(name = "httpContentType", description = "MIME формат файла"),
                @Parameter(name = "bucketName", description = "Название бакета. Если не задано, то сервер сохранит файл в бакет, указанный в свойствах внешней конфигурации")
        },
        responses = {
                @ApiResponse(
                        description = "Загружен массив байт из объектного хранилища",
                        responseCode = "201"
                ),
                @ApiResponse(
                        description = "Если указанные бакет, пользователь или объект не существуют",
                        responseCode = "400",
                        content = {@Content(
                                mediaType = "application/json"
                        )}

                ),
                @ApiResponse(
                        description = "В случае если в URL адресе не указаны имя пользователя и/или идентификатор объекта",
                        responseCode = "404",
                        content = {@Content(
                                mediaType = "application/json"
                        )}
                ),
                @ApiResponse(
                        description = "Если на сервере в ходе обработки запроса возникло необработанное исключение",
                        responseCode = "500",
                        content = {@Content(
                                mediaType = "application/json"
                        )}
                )
        }

    )
    public ResponseEntity<Resource> download(
        @PathVariable(name = "username") String username,
        @PathVariable(name = "objectName") String objectName,

        @RequestParam(name = "content_type", required = false) String httpContentType,
        @RequestParam(name = "bucket_name", required = false) String bucketName
    ) {
        final ByteArrayResource file = service.get(username, objectName, bucketName);

        MediaType contentType;
        try {
            contentType = MediaType.valueOf(httpContentType);
        } catch (InvalidMediaTypeException ex) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
            .contentLength(file.contentLength())
            .contentType(contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
        .body(file);
    }


    @DeleteMapping("/{username}/{objectName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Удалить объект",
        parameters = {
                @Parameter(name = "username", description = "Имя пользователя (без символа \"/\"), которому принадлежит загруженный объект", required = true),
                @Parameter(name = "objectName", description = "Идентификатор объекта в формате \"UUID.*\"", required = true),

                @Parameter(name = "bucketName", description = "Название бакета. Если не задано, то сервер сохранит файл в бакет, указанный в свойствах внешней конфигурации")
        },
        responses = {
                @ApiResponse(
                        description = "Объект успешно удален",
                        responseCode = "201",
                        content = {@Content(
                                mediaType = "application/json"
                        )}
                ),
                @ApiResponse(
                        description = "Если указанные бакет, пользователь или объект не существуют",
                        responseCode = "400",
                        content = {@Content(
                                mediaType = "application/json"
                        )}

                ),
                @ApiResponse(
                        description = "В случае если в URL адресе не указаны имя пользователя и/или идентификатор объекта",
                        responseCode = "404",
                        content = {@Content(
                                mediaType = "application/json"
                        )}
                ),
                @ApiResponse(
                        description = "Если на сервере в ходе обработки запроса возникло необработанное исключение",
                        responseCode = "500",
                        content = {@Content(
                                mediaType = "application/json"
                        )}
                )
        }
    )
    public ResponseEntity<String> delete(
        @PathVariable(name = "username") String username,
        @PathVariable(name = "objectName") String objectName,

        @RequestParam(name = "bucket_name", required = false) String bucketName
    ) {
        return ResponseEntity.ok(service.delete(username, objectName, bucketName));
    }
}
