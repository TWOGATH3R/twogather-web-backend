package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    public enum AuthenticationExceptionErrorCode {
        AUTHORIZATION_HEADER_UNINVOLVED("인증 헤더가 빠져있거나 요청이 유효하지 않습니다"),
        TOKEN_EXPIRED("토큰이 만료되었습니다"),
        UNAUTHORIZED("인증된 사용자가 아닙니다"),
        INVALID_TOKEN("토큰이 유효하지 않습니다"),
        LOGIN_FAILURE("로그인 과정 중 실패하였습니다");
        private final String message;
        private final HttpStatus status;

        AuthenticationExceptionErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.UNAUTHORIZED;
        }
        AuthenticationExceptionErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final AuthenticationExceptionErrorCode errorCode;

    public CustomAuthenticationException(AuthenticationExceptionErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public AuthenticationExceptionErrorCode getErrorCode() {
        return errorCode;
    }
}
