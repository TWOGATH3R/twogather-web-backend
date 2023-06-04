package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

public class CustomAccessDeniedException extends AccessDeniedException {
    public enum AccessDeniedExceptionErrorCode {
        ACCESS_DENIED("권한이 없습니다");
        private final String message;
        private final HttpStatus status;

        AccessDeniedExceptionErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.FORBIDDEN;
        }
        AccessDeniedExceptionErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final AccessDeniedExceptionErrorCode errorCode;

    public CustomAccessDeniedException(AccessDeniedExceptionErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public AccessDeniedExceptionErrorCode getErrorCode() {
        return errorCode;
    }
}
