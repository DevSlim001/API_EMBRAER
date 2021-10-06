package com.slim.manual.rest.dto;

import com.slim.manual.domain.model.Manual;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ManualDTO {

    private Integer codManual;

    @NotEmpty(message = "{campo.partNumber.obrigatorio}")
    private String partNumber;

    @NotEmpty(message = "{campo.nomeManual.obrigatorio}")
    private String nome;
    
    public Manual toEntityInsert(){
        new Manual();
        return Manual
                    .builder()
                    .codManual(codManual)
                    .partNumber(partNumber)
                    .nome(nome)
                    .build();
    }
}
