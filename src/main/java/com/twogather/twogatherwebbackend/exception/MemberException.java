package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends ClientException{
    public enum MemberErrorCode {
        DUPLICATE_USERNAME("로그인 아이디가 중복됩니다"),
        DUPLICATE_EMAIL("이메일이 중복됩니다"),
        NO_SUCH_MEMBER_ID("해당 멤버가 존재하지 않습니다", HttpStatus.NOT_FOUND),
        NO_SUCH_MEMBER("해당 정보로 가입된 사용자가 없습니다", HttpStatus.FORBIDDEN),
        DUPLICATE_NICKNAME("닉네임이 중복됩니다");
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
