package com.twogather.twogatherwebbackend.exception;

import org.springframework.http.HttpStatus;

public abstract class ClientException  extends RuntimeException {
    public ClientException(String errorMessage){
        super(errorMessage);
    }
    abstract public HttpStatus getStatus();
}
