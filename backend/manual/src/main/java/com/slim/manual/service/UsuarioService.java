package com.slim.manual.service;

import javax.transaction.Transactional;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.domain.repository.UsuarioRepository;
import com.slim.manual.rest.dto.UsuarioDTO;
import com.slim.manual.utils.GeradorSenha;
import com.slim.manual.utils.SenderMailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final PasswordEncoder passwordEncoder ;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    SenderMailService senderMailService;
    



    /**
     * Cria um usuário
     * @param Usuario
     * @return UsuarioDTO
     */
    @Transactional
    public UsuarioDTO create(UsuarioDTO usuarioDTO){
        GeradorSenha geradorSenha = new GeradorSenha();
        String senha = geradorSenha.gerarSenha();
        String senhaCripto = passwordEncoder.encode(senha);
        //String senhaCripto = passwordEncoder.encode(usuario.getSenha());
        Usuario usuario = usuarioDTO.toEntityInsert();
        usuario.setSenha(senhaCripto);
        senderMailService.enviar(usuario.getEmail(),"Conta criada","Sua conta foi criada.\nSua senha é: "+senha);
        return usuarioRepository
                .save(usuario)
                .toUserDTO();
    }

    
}
