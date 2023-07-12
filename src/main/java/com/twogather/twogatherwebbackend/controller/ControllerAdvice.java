package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.twogather.twogatherwebbackend.dto.common.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.*;
import com.twogather.twogatherwebbackend.log.CachingRequestBodyFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.twogather.twogatherwebbackend.exception.InvalidArgumentException.InvalidArgumentErrorCode.INVALID_ARGUMENT;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> handleFileException(HttpServletRequest request, FileException ex) {
        logError(request,ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<?> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex));
    }

    @ExceptionHandler({ClientException.class, MissingServletRequestPartException.class, MultipartException.class})
    public ResponseEntity<ErrorResponse> clientExceptionHandler(HttpServletRequest request, ClientException e) {
        logError(request,e);
        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.of(e));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(HttpServletRequest request, AccessDeniedException e) {
        logError(request,e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.of(e));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationExceptionHandler(HttpServletRequest request, AuthenticationException e) {
        logError(request,e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(e));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        logError(request,e);
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

    @ExceptionHandler({InvalidFormatException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> invalidFormatHandler(final HttpServletRequest request, final Exception exception) {
        logError(request, exception);
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    //TODO:나중에 변경
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledExceptionHandler(final HttpServletRequest request, final Exception exception) {
        logError(request, exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(exception));
    }
    private void logError(HttpServletRequest request, Exception e){
        e.printStackTrace();

        String requestBody = CachingRequestBodyFilter.getRequestBody().orElse("");

        String maskedRequestBody = maskSensitiveFields(requestBody);

        log.error("{}: Request body: {}", Thread.currentThread().getId(), maskedRequestBody);
        log.error("{}: Request URL: {}", Thread.currentThread().getId(), request.getRequestURL());
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            log.error("{}: User: {}", Thread.currentThread().getId(), principal.getName());
        }
        log.error("An error occurred while processing the request", e);
    }
    private String maskSensitiveFields(String requestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            maskFieldsContainingWord(jsonNode, "business");
            maskFieldsContainingWord(jsonNode, "password");
            maskEmailField(jsonNode);

            return jsonNode.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return requestBody;
        }
    }

    private void maskFieldsContainingWord(JsonNode jsonNode, String word) {
        jsonNode.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();

            if (fieldName.contains(word)) {
                ((ObjectNode) jsonNode).put(fieldName, "********");
            }

            if (fieldValue.isObject()) {
                maskFieldsContainingWord(fieldValue, word);
            } else if (fieldValue.isArray()) {
                fieldValue.forEach(arrayItem -> maskFieldsContainingWord(arrayItem, word));
            }
        });
    }

    private void maskEmailField(JsonNode jsonNode) {
        if (jsonNode.has("email")) {
            ((ObjectNode) jsonNode).put("email", "********");
        }
    }
}
