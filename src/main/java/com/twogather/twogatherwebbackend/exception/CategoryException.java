package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class CategoryException extends ClientException{
    public enum CategoryErrorCode {
        NO_SUCH_CATEGORY("해당하는 카테고리가 존재하지않습니다", HttpStatus.NOT_FOUND);
        private final String message;
        private final HttpStatus status;

        CategoryErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        CategoryErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final CategoryErrorCode errorCode;

    public CategoryException(CategoryErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;

    }
    public HttpStatus getStatus() {return errorCode.status;}
    public CategoryErrorCode getErrorCode(){
        return errorCode;
    }
}
