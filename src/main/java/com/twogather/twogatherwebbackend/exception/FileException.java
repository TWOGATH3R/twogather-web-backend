package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class FileException  extends RuntimeException {
    public enum FileErrorCode {
        FILE_NOT_FOUND("파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
        FILE_READ_ERROR("파일 읽기 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
        FILE_WRITE_ERROR("파일 쓰기 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
        FILE_CONVERTER_ERROR("파일 변환 오류가 발생했습니다. 잠시후에 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

        private final String message;
        private final HttpStatus status;

        FileErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    private final FileErrorCode errorCode;

    public FileException(FileErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public FileErrorCode getErrorCode() {
        return errorCode;
    }
}