package com.slim.manual.exception;

public class ValidateTokenException extends RuntimeException{
    public ValidateTokenException(String message){
        super(message);
    }
}
