package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class StoreException extends ClientException{
    public enum StoreErrorCode {
        DUPLICATE_NAME("이름이 중복됩니다"),
        NO_SUCH_STORE("해당하는 가게가 존재하지 않습니다", HttpStatus.NOT_FOUND),
        INVALID_STORE_TYPE("유효하지 않은 StoreSearchType 입니다"),
        BIZ_REG_NUMBER_VALIDATION("사업자등록번호 검증에 실패하였습니다"),
        ALREADY_APPROVED_STORE("이미 승인된 가게 입니다");

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
