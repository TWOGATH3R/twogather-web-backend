package com.twogather.twogatherwebbackend.exception;

public class SystemException extends RuntimeException{
    private enum SystemErrorCode {
        INVALID_PARAM("유효하지 않은 인자입니다");

        private final String message;

        SystemErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){
            return message;
        }
    }
    private final SystemErrorCode errorCode;
    public SystemException(SystemErrorCode errorCode){
        this.errorCode = errorCode;
    }
    public SystemErrorCode getErrorCode(){
        return errorCode;
    }
}
