package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class SQLException extends ClientException{
    public enum SQLErrorCode {
        INVALID_REQUEST_PARAM("유효하지 않은 요청 파리미터 입니다");

        private final String message;
        private final HttpStatus status;

        SQLErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        SQLErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final SQLErrorCode errorCode;
    public SQLException(SQLErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public SQLErrorCode getErrorCode(){
        return errorCode;
    }
}
