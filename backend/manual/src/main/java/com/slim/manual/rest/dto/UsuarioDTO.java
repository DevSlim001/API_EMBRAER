package com.slim.manual.rest.dto;
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
    private Integer codUsuario;
    private String nome;
    private String email;
    private String role;

    public Usuario toEntityInsert(){
        new Usuario();
        return Usuario
                    .builder()
                    .codUsuario(codUsuario)
                    .email(email)
                    .nome(nome)
                    .role(role)
                    .build();
    }
}
