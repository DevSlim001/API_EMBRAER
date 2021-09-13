package com.slim.manual.rest.dto;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.slim.manual.domain.model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class UsuarioDTO {
    
    @NotEmpty(message = "{campo.nome.obrigatorio}")
    private String nome;

    @NotEmpty(message = "{campo.email.obrigatorio}")
    @Email(message = "{campo.email.valido}")
    private String email;

    @NotEmpty(message = "{campo.role.obrigatorio}")
    private String role;

    public Usuario toEntityInsert(){
        new Usuario();
        return Usuario
                    .builder()
                    .email(email)
                    .nome(nome)
                    .role(role)
                    .build();
    }
}
