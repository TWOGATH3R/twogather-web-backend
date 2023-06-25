package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class KeywordException extends ClientException{
    public enum KeywordErrorCode {
        MAXIMUM_KEYWORD_LIMIT("정해진 개수만큼의 키워드만 입력할 수 있습니다"),
        DUPLICATE_KEYWORD("키워드가 중복됩니다"),
        NO_SUCH_KEYWORD("해당하는 키워드가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        private final String message;
        private final HttpStatus status;

        KeywordErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        KeywordErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final KeywordErrorCode errorCode;
    public KeywordException(KeywordErrorCode errorCode, HttpStatus status){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public KeywordException(KeywordErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public KeywordErrorCode getErrorCode(){
        return errorCode;
    }
}
