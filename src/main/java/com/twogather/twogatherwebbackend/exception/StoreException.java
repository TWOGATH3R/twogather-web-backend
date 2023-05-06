package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class StoreException extends ClientException{

    public enum StoreErrorCode {
        DUPLICATE_NAME("이름이 중복됩니다"),
        STORE_NOT_FOUND("해당하는 가게가 존재하지 않습니다", HttpStatus.NOT_FOUND);

        private final String message;
        private final HttpStatus status;

        StoreErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        StoreErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final StoreErrorCode errorCode;
    public StoreException(StoreErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public StoreErrorCode getErrorCode(){
        return errorCode;
    }
}
