package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import static com.twogather.twogatherwebbackend.exception.InvalidArgumentException.InvalidArgumentErrorCode.INVALID_ARGUMENT;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> handleFileException(HttpServletRequest request, FileException ex) {
        logInfo(request,ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "잘못된 요청입니다. 요청 데이터의 형식이 올바르지 않습니다.";

        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause instanceof DateTimeParseException) {
            errorMessage = "잘못된 시간 형식입니다. 올바른 시간 형식으로 다시 시도해주세요.";
        } else if (rootCause instanceof InvalidFormatException) {
            errorMessage = "잘못된 데이터 형식입니다. 올바른 데이터 형식으로 다시 시도해주세요.";
        } else if (rootCause instanceof MissingServletRequestParameterException) {
            errorMessage = "필수 필드가 누락되었습니다. 필수 필드를 모두 입력해주세요.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errorMessage));
    }
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> clientExceptionHandler(HttpServletRequest request, ClientException e) {
        logInfo(request,e);
        return ResponseEntity.badRequest().body(ErrorResponse.of(e));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(HttpServletRequest request, AccessDeniedException e) {
        logInfo(request,e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.of(e));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationExceptionHandler(HttpServletRequest request, AuthenticationException e) {
        logInfo(request,e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(e));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
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
        return ResponseEntity.badRequest().body(ErrorResponse.of(INVALID_ARGUMENT.getMessage(),errors));
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
