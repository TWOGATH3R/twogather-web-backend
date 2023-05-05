package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import com.twogather.twogatherwebbackend.exception.ClientException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> clientExceptionHandler(HttpServletRequest request, ClientException e) {
        logInfo(request,e);
        return ResponseEntity.badRequest().body(ErrorResponse.of(e));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        logInfo(request,e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName;
            String errorMessage = error.getDefaultMessage();

            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            } else {
                fieldName = error.getObjectName();
            }
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledExceptionHandler(final HttpServletRequest request, final Exception exception) {
        logWarn(request, exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.internalServerError());
    }

    private void logWarn(HttpServletRequest request, Exception e){
        log.warn("An error occurred while processing the request", e);
        log.warn("Request URL: {}", request.getRequestURL());
        log.warn("Request method: {}", request.getMethod());
    }
    private void logInfo(HttpServletRequest request, Exception e){
        log.info("error message: {}", e.getMessage());
        log.info("An error occurred while processing the request", e);
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request method: {}", request.getMethod());
    }
}
