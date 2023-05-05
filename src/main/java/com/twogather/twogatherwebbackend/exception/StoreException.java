package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class StoreException extends ClientException{

    public enum StoreErrorCode {
        DUPLICATE_NAME("이름이 중복됩니다"),
        STORE_NOT_FOUND("해당하는 가게가 존재하지 않습니다");

        private final String message;

        StoreErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){ return message; }
    }
    private final HttpStatus status;
    private final StoreErrorCode errorCode;
    public StoreException(StoreErrorCode errorCode, HttpStatus status){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;
    }
    public StoreException(StoreErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }
    public HttpStatus getStatus() {return status;}
    public StoreErrorCode getErrorCode(){
        return errorCode;
    }
}
