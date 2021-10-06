package com.slim.manual.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.slim.manual.ApiErrors;
import com.slim.manual.exception.CredencialException;
import com.slim.manual.exception.ManualNotFoundException;
import com.slim.manual.exception.UploadCodelistException;
import com.slim.manual.exception.UsuarioNotFoundException;
import com.slim.manual.exception.UsuarioExistenteException;
import com.slim.manual.exception.ValidateTokenException;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Classe responsável pelo tratamento de erros
 */
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(CredencialException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrors handleCredencialException(CredencialException ex){
        String mensagem = ex.getMessage();
        return new ApiErrors(mensagem);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrors handleUsuarioNotFoundException(UsuarioNotFoundException ex){
        String mensagem = ex.getMessage();
        return new ApiErrors(mensagem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleMethodNotValidException(MethodArgumentNotValidException ex){
        List<String> errors = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(erro-> erro.getDefaultMessage())
                .collect(Collectors.toList());
        return new ApiErrors(errors);
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleUsuarioExistenteException(UsuarioExistenteException ex){
        String mensagem = ex.getMessage();
        return new ApiErrors(mensagem);
    }

    @ExceptionHandler(ValidateTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrors handleValidateTokenException(ValidateTokenException ex){
        String mensagem = ex.getMessage();
        return new ApiErrors(mensagem);
    }
    @ExceptionHandler(UploadCodelistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleUploadCodelistException(UploadCodelistException ex){
        String mensagem = ex.getMessage();
        return new ApiErrors(mensagem);
    }

    @ExceptionHandler(ManualNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrors handleManualNotFoundException(ManualNotFoundException ex){
        String mensagem = ex.getMessage();
        return new ApiErrors(mensagem);
    }

    
}


