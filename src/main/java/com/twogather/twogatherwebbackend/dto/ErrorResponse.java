package com.twogather.twogatherwebbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.MethodArgumentNotValidException;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;

    public ErrorResponse() {

    }

    public ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse of(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse of(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public String getMessage() {
        return message;
    }
}