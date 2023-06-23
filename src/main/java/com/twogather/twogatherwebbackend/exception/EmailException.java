package com.twogather.twogatherwebbackend.exception;


import org.springframework.http.HttpStatus;

public class EmailException extends ClientException {
    public enum EmailErrorCode {
        EMAIL_VERIFICATION_REQUEST_IS_AVAILABLE_ONLY_ONCE_EVERY_5_MINUTES("이메일 인증 요청 메일은 5분에 한번씩만 보낼 수 있습니다"),
        EMAIL_SENDING_ERROR("이메일을 보내는 과정에서 오류가 발생했습니다"),
        INVALID_CODE("이메일 코드가 유효하지 않습니다");
        private final String message;

        EmailErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){ return message; }
    }
    private final HttpStatus status;
    private final EmailErrorCode errorCode;
    public EmailException(EmailErrorCode errorCode, HttpStatus status){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;
    }
    public EmailException(EmailErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }
    public HttpStatus getStatus() {return status;}
    public EmailErrorCode getErrorCode(){
        return errorCode;
    }
}
