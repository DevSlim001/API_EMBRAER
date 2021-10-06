package com.slim.manual.exception;

public class ManualNotFoundException extends RuntimeException{
    public ManualNotFoundException(String message){
        super(message);
    }
}
