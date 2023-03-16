package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userException(HttpServletRequest request, UserException e) {
        log(request,e);
        UserException.UserErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(e);
        switch (errorCode) {
            case BIZ_REG_NUMBER_VALIDATION:
                return ResponseEntity.badRequest().body(errorResponse);
            default:
                ErrorResponse unexpectedError = new ErrorResponse("서버에서 예상치 못한 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(unexpectedError);
        }
    }
    private void log(HttpServletRequest request, Exception e){
        log.error("An error occurred while processing the request", e);
        log.error("Request URL: {}", request.getRequestURL());
        log.error("Request method: {}", request.getMethod());
    }
}
