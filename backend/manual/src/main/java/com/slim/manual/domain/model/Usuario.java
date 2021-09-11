package com.slim.manual.domain.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import com.slim.manual.rest.dto.UsuarioDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "usuario")
@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codUsuario;

    @Column
    @NotEmpty(message = "{campo.nome.obrigatorio}")
    private String nome;

    @Column
    @NotEmpty(message = "{campo.senha.obrigatorio}")
    private String senha;

    @Column(unique = true)
    @NotEmpty(message = "{campo.email.obrigatorio}")
    private String email;

    @Column
    @NotEmpty(message = "{campo.role.obrigatorio}")
    private String role;

    public UsuarioDTO toUserDTO(){
        return UsuarioDTO
                    .builder()
                    .codUsuario(this.codUsuario)
                    .nome(this.nome)
                    .email(this.email)
                    .role(this.role)
                    .build();

    }
}
