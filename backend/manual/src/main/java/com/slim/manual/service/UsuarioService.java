package com.slim.manual.service;

import javax.transaction.Transactional;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.domain.repository.UsuarioRepository;
import com.slim.manual.exception.SenhaInvalidaException;
import com.slim.manual.rest.dto.CredenciaisDTO;
import com.slim.manual.rest.dto.TokenDTO;
import com.slim.manual.rest.dto.UsuarioDTO;
import com.slim.manual.security.jwt.JwtService;
import com.slim.manual.utils.GeradorSenha;
import com.slim.manual.utils.SenderMailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService{
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    SenderMailService senderMailService;
    
    private final PasswordEncoder passwordEncoder ;
    
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
        Usuario usuario = usuarioDTO.toEntityInsert();
        usuario.setSenha(senhaCripto);
        senderMailService.enviar(usuario.getEmail(),"Conta criada","Sua conta foi criada.\nSua senha é: "+senha);
        return usuarioRepository
                .save(usuario)
                .toUserDTO();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(email);
        Usuario usuario = usuarioRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado nos registros."));
        String[] roles = usuario.getRole().equals("ADMIN") ? 
                        new String[] {"ADMIN","USER"} : 
                        new String[] {"USER"} ;
        return User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(roles)
                .build();
    }

    public TokenDTO auth(CredenciaisDTO credenciais){

        UserDetails user = loadUserByUsername(credenciais.getEmail());
        boolean senhasIguais = passwordEncoder.matches(credenciais.getSenha(), user.getPassword());
        if(senhasIguais){
            Usuario usuario = usuarioRepository
                                .findByEmail(credenciais.getEmail())
                                .get();
            String token = jwtService.gerarToken(usuario);
            return TokenDTO
                        .builder()
                        .token(token)
                        .build();
        }
        throw new SenhaInvalidaException();
    }
}
