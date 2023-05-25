package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public class ReviewException extends ClientException{

    public enum ReviewErrorCode {

    }

    public ReviewException(String errorMessage) {
        super(errorMessage);
    }
}
