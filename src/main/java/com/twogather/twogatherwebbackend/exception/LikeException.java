package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class LikeException extends ClientException{
    public enum LikeErrorCode {
        DUPLICATE_LIKE("중복으로 좋아요를 누를 수 없습니다");

        private final String message;
        private final HttpStatus status;

        LikeErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        LikeErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final LikeErrorCode errorCode;
    public LikeException(LikeErrorCode errorCode, HttpStatus status){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public LikeException(LikeErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public LikeErrorCode getErrorCode(){
        return errorCode;
    }
}
