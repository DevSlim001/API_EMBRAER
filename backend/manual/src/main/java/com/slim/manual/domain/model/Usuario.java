package com.slim.manual.domain.model;

import javax.persistence.*;

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
    private String nome;

    @Column
    private String senha;

    @Column(unique = true)
    private String email;

    @Column
    private String role;

    public UsuarioDTO toUserDTO(){
        return UsuarioDTO
                    .builder()
                    .nome(this.nome)
                    .email(this.email)
                    .role(this.role)
                    .build();

    }
}
