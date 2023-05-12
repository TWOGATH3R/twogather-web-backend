package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class InvalidArgumentException extends MethodArgumentNotValidException {
    public enum InvalidArgumentErrorCode {
        INVALID_ARGUMENT("유효하지않은 값을 입력하였습니다"),
        BIZ_REG_NUMBER_VALIDATION("사업자등록번호 검증에 실패하였습니다");
        private final String message;
        private final HttpStatus status;

        InvalidArgumentErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        InvalidArgumentErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final InvalidArgumentErrorCode errorCode;

    public InvalidArgumentException(InvalidArgumentErrorCode errorCode, BindingResult bindingResult) {
        super(null, bindingResult);
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public InvalidArgumentErrorCode getErrorCode(){
        return errorCode;
    }
}

