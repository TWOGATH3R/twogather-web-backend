package com.twogather.twogatherwebbackend.exception;

public abstract class ClientException  extends RuntimeException {
    public ClientException(String errorMessage){
        super(errorMessage);
    }
}
