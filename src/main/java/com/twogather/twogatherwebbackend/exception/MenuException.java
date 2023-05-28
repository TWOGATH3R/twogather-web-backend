package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class MenuException extends ClientException{
    public enum MenuErrorCode {
        NO_SUCH_MENU("해당하는 메뉴가 없습니다",  HttpStatus.NOT_FOUND);

        private final String message;
        private final HttpStatus status;

        MenuErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        MenuErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final MenuErrorCode errorCode;
    public MenuException(MenuErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public MenuErrorCode getErrorCode(){
        return errorCode;
    }
}
