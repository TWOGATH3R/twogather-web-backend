package com.twogather.twogatherwebbackend.controller.exception;

import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.StoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class StoreExceptionHandler {
    @ExceptionHandler(StoreException.class)
    public ResponseEntity<ErrorResponse> storeException(HttpServletRequest request, StoreException e) {
        log(request,e);
        ErrorResponse errorResponse = ErrorResponse.of(e);
        return ResponseEntity.badRequest().body(errorResponse);
    }
    private void log(HttpServletRequest request, StoreException e){
        log.error("errorCode: {}", e.getErrorCode());
        log.error("An error occurred while processing the request", e);
        log.error("Request URL: {}", request.getRequestURL());
        log.error("Request method: {}", request.getMethod());
    }
}
