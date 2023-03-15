package com.twogather.twogatherwebbackend.controller;

import aj.org.objectweb.asm.ConstantDynamic;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JSONException.class)
    public ResponseEntity<String> handleJsonException(JSONException e, HttpServletRequest request) {
        String body = "";
        try {
            body = request.getInputStream().toString();
        } catch (IOException ex) {
            log.error("Error reading request body: " + ex.getMessage(), ex);
        }
        log.error("JSONException occurred while parsing JSON data: " + e.getMessage() + "\n" + "JSON data: " + body, e);

        log.error("JSONException occurred: " + e.getMessage(), e);
        return new ResponseEntity<>("JSON error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // 그 외의 예외 발생 시 처리할 로직을 작성합니다.
        return new ResponseEntity<>("`An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}