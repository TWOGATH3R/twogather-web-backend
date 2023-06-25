package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class ReviewException extends ClientException{

    public enum ReviewErrorCode {
        NO_SUCH_REVIEW("해당 리뷰가 존재하지 않습니다", HttpStatus.NOT_FOUND);

        private final String message;
        private final HttpStatus status;

        ReviewErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }

        ReviewErrorCode(String message, HttpStatus status) {
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

    private final ReviewErrorCode errorCode;
    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.status;
    }
    public ReviewErrorCode getErrorCode() {
        return errorCode;
    }
}
