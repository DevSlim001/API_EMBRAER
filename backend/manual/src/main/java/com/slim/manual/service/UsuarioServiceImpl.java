package com.slim.manual.service;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.domain.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UserDetailsService{
    
    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
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
}
