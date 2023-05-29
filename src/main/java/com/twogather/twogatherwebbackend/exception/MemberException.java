package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends ClientException{
    public enum MemberErrorCode {
        DUPLICATE_USERNAME("로그인 아이디가 중복됩니다"),
        NO_SUCH_MEMBER_ID("해당 멤버가 존재하지 않습니다"),
        NO_SUCH_USERNAME("해당 ID로 가입된 사용자가 없습니다", HttpStatus.NOT_FOUND),
        PASSWORD_MISMATCH("비밀번호가 틀렸습니다"),
        MEMBER_NOT_ACTIVE("해당 사용자가 활성화 되어있지 않습니다");
        private final String message;
        private final HttpStatus status;

        MemberErrorCode(String message) {
            this.message = message;
            this.status = HttpStatus.BAD_REQUEST;
        }
        MemberErrorCode(String message, HttpStatus status) {
            this.message = message;
            this.status = status;
        }
        public String getMessage(){ return message; }
        public HttpStatus getStatus(){ return status; }
    }
    private final MemberErrorCode errorCode;
    public MemberException(MemberErrorCode errorCode, HttpStatus status){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public MemberException(MemberErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {return errorCode.status;}
    public MemberErrorCode getErrorCode(){
        return errorCode;
    }
}
