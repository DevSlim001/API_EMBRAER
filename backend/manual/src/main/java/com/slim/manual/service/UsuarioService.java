package com.slim.manual.service;

import javax.transaction.Transactional;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.domain.repository.UsuarioRepository;
import com.slim.manual.rest.dto.UsuarioDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;
    
    private final PasswordEncoder passwordEncoder ;

    /**
     * Cria um usuário
     * @param Usuario
     * @return UsuarioDTO
     */
    @Transactional
    public UsuarioDTO create(Usuario usuario){
        String senhaCripto = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCripto);
        return usuarioRepository
                .save(usuario)
                .toUserDTO();
    }

    
}
