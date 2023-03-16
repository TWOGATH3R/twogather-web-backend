package com.twogather.twogatherwebbackend.dto;

import com.twogather.twogatherwebbackend.exception.UserException;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ErrorResponse {
    private String message;

    public ErrorResponse() {

    }

    public ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse of(final UserException exception){
        return new ErrorResponse(exception.getMessage());
    }
    public static ErrorResponse of(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse of(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }

    public String getMessage() {
        return message;
    }
}