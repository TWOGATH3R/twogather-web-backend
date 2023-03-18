package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class MemberExceptionHandler {
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> memberException(HttpServletRequest request, MemberException e) {
        log(request,e);
        ErrorResponse errorResponse = ErrorResponse.of(e);
        return ResponseEntity.badRequest().body(errorResponse);
    }
    private void log(HttpServletRequest request, MemberException e){
        log.error("errorCode: {}", e.getErrorCode());
        log.error("An error occurred while processing the request", e);
        log.error("Request URL: {}", request.getRequestURL());
        log.error("Request method: {}", request.getMethod());
    }
}
