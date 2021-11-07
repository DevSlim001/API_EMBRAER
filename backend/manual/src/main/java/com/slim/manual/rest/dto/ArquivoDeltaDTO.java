package com.slim.manual.rest.dto;

import org.springframework.core.io.ByteArrayResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor @Builder
public class ArquivoDeltaDTO {
    private String nomeArquivo;
    private byte[] conteudo;
    /* private ByteArrayResource conteudo; */
}
