package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class CommentException extends ClientException{
    public enum CommentErrorCode {
        NO_SUCH_COMMENT("해당하는 대댓글이 존재하지않습니다", HttpStatus.NOT_FOUND);
        private final String message;
        private final HttpStatus status;

        CommentErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        CommentErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final CommentErrorCode errorCode;

    public CommentException(CommentErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;

    }
    public HttpStatus getStatus() {return errorCode.status;}
    public CommentErrorCode getErrorCode(){
        return errorCode;
    }
}
