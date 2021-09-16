package com.slim.manual.exception;

public class UsuarioExistenteException extends RuntimeException{
    public UsuarioExistenteException(String message){
        super(message);
    }
}
