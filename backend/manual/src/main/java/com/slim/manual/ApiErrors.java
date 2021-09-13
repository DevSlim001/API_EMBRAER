package com.slim.manual;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public class ApiErrors {

    @Getter
    private List<String> errors;

    public ApiErrors(String mensagem){
        this.errors = Arrays.asList(mensagem);
    }

    public ApiErrors(List<String> errors){
        this.errors = errors;
    }

}
