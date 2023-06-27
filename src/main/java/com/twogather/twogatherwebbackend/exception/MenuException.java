package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class MenuException extends ClientException{
    public enum MenuErrorCode {
        NO_SUCH_MENU("해당하는 메뉴가 없습니다",  HttpStatus.NOT_FOUND),
        DUPLICATE_NAME("이미 해당 이름으로 가게에 메뉴가 등록되어 있습니다"),
        MAX_MENU_SIZE("메뉴 최대 등록 개수를 넘었습니다");

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
