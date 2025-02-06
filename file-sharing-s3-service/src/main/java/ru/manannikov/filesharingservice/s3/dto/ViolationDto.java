package ru.manannikov.filesharingservice.s3.dto;

public record ViolationDto(
    String fieldName,
    String message
) {}
