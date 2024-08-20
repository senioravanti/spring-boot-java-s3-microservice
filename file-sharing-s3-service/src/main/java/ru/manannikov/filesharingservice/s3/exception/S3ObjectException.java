package ru.manannikov.filesharingservice.s3.exception;

public class S3ObjectException extends RuntimeException {
    public S3ObjectException(String message) {
        super(message);
    }
}
