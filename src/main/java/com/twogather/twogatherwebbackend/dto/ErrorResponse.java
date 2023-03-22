package com.twogather.twogatherwebbackend.dto;

import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ErrorResponse {
    private String message;

    public ErrorResponse() {

    }

    public ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse of(final MemberException exception){
        return new ErrorResponse(exception.getErrorCode().getMessage());
    }
    public static ErrorResponse of(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }
    public static ErrorResponse of(final BusinessHourException exception) {
        return new ErrorResponse(exception.getErrorCode().getMessage());
    }


    public static ErrorResponse of(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }

    public String getMessage() {
        return message;
    }
}