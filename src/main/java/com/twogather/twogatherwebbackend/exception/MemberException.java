package com.twogather.twogatherwebbackend.exception;

public class MemberException extends RuntimeException{

    public enum MemberErrorCode {
        DUPLICATE_EMAIL("이메일이 중복됩니다"),
        BIZ_REG_NUMBER_VALIDATION("사업자등록번호 검증에 실패하였습니다"),
        NO_SUCH_EMAIL("해당 이메일로 가입된 사용자가 없습니다"),
        PASSWORD_MISMATCH("비밀번호가 틀렸습니다");

        private final String message;

        MemberErrorCode(String message) {
            this.message = message;
        }
        public String getMessage(){ return message; }
    }
    private final MemberErrorCode errorCode;
    public MemberException(MemberErrorCode errorCode){
        this.errorCode = errorCode;
    }
    public MemberErrorCode getErrorCode(){
        return errorCode;
    }
}
