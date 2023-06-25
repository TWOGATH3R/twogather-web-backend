package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class BusinessHourException extends ClientException{
    public enum BusinessHourErrorCode {
        NO_SUCH_BUSINESS_HOUR_BY_STORE_ID("해당 가게의 영업시간 정보가 없습니다", HttpStatus.NOT_FOUND),
        NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID("해당 영업시간 정보는 존재하지 않습니다", HttpStatus.NOT_FOUND),
        DUPLICATE_DAY_OF_WEEK("중복되는 요일이 있습니다"),
        DUPLICATE_BUSINESS_HOUR("이미 영업시간이 저장되어 있습니다. 업데이트를 수행해 주세요"),
        INVALID_TIME("시간정보가 유효하지 않습니다."),
        MUST_HAVE_START_TIME_AND_END_TIME("시작시작과 종료시간을 설정해주세요"),
        START_TIME_MUST_BE_BEFORE_END_TIME("시작시간은 종료시간보다 먼저여야합니다"),
        INVALID_BUSINESS_HOUR("영업시간은 요일별로 7개여야합니다");

        private final String message;
        private final HttpStatus status;

        BusinessHourErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        BusinessHourErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final BusinessHourErrorCode errorCode;

    public BusinessHourException(BusinessHourErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;

    }
    public HttpStatus getStatus() {return errorCode.status;}
    public BusinessHourErrorCode getErrorCode(){
        return errorCode;
    }
}
