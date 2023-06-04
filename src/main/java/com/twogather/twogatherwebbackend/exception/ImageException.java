package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class ImageException  extends ClientException {
    public enum ImageErrorCode {
        NO_SUCH_IMAGE("해당 사진을 찾을 수 없습니다");
        private final String message;
        private final HttpStatus status;

        ImageErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        ImageErrorCode(String message, HttpStatus status) {
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

    private final ImageErrorCode errorCode;

    public ImageException(ImageErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public ImageErrorCode getErrorCode() {
        return errorCode;
    }
}