package com.slim.manual.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class CodelistLinha {

    private String numSecao;
    
    private String numSubSecao;
    
    private String numBloco;
    
    private String nomeBloco;
    
    private String codBlocoCodelist;

    private List<String> tracos;

}
