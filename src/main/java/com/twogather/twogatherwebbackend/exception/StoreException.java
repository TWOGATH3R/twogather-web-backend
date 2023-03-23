package com.twogather.twogatherwebbackend.exception;

public class StoreException extends RuntimeException{

    public enum StoreErrorCode {
        DUPLICATE_NAME("이름이 중복됩니다"),
        STORE_NOT_FOUND("해당하는 가게가 존재하지 않습니다");

        private final String message;

        StoreErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){ return message; }
    }
    private final StoreErrorCode errorCode;
    public StoreException(StoreErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public StoreErrorCode getErrorCode(){
        return errorCode;
    }
}
