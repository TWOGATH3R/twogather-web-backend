package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class BusinessHourException extends ClientException{
    public enum BusinessHourErrorCode {
        NO_SUCH_BUSINESS_HOUR_BY_STORE_ID("해당 가게의 영업시간 정보가 없습니다"),
        NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID("해당 영업시간 정보는 존재하지 않습니다"),
        DUPLICATE_DAY_OF_WEEK("이미 해당 요일에 대한 영업 시간이 등록되어 있습니다"),
        INVALID_TIME("시간정보가 유효하지 않습니다.");
        private final String message;

        BusinessHourErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){ return message; }
    }
    private final BusinessHourErrorCode errorCode;
    private final HttpStatus status;

    public BusinessHourException(BusinessHourErrorCode errorCode, HttpStatus status){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;

    }
    public BusinessHourException(BusinessHourErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;

    }
    public HttpStatus getStatus() {return status;}
    public BusinessHourErrorCode getErrorCode(){
        return errorCode;
    }
}
