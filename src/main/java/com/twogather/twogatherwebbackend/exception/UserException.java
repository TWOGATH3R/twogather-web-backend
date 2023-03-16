package com.twogather.twogatherwebbackend.exception;

public class UserException extends RuntimeException{

    public enum UserErrorCode {
        DUPLICATE_EMAIL("이메일이 중복됩니다"),
        BIZ_REG_NUMBER_VALIDATION("사업자등록번호 검증에 실패하였습니다");

        private final String message;

        UserErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){ return message; }
    }
    private final UserErrorCode errorCode;
    public UserException(UserErrorCode errorCode){
        this.errorCode = errorCode;
    }
    public UserErrorCode getErrorCode(){
        return errorCode;
    }
}
