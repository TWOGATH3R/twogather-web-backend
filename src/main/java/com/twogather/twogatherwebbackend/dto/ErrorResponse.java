package com.twogather.twogatherwebbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ErrorResponse {
    private String message;
    private Map<String, String> errors;
    public ErrorResponse() {
    }
    public ErrorResponse(final String message) {
        this.message = message;
    }

    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
    public static ErrorResponse of(String message, Map<String, String> errors) {
        return new ErrorResponse(message, errors);
    }

    public static ErrorResponse of(final Exception exception) {
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
}