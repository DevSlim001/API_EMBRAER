package com.slim.manual.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor @Builder
public class ModificacaoBloco {
    private BlocoRevisao blocoRevisao;
    private String paginaBloco;
    private String operacao;
    private String revisaoNome;
}
