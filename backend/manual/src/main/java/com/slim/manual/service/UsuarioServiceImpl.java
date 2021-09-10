package com.slim.manual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl /* implements UserDetailsService */{
    
    @Autowired
    private PasswordEncoder encoder;


    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User
                .builder()
                .username(username)
                .password(encoder.encode(password))
                .roles("USER","ADMIN")
                .build();
    } */
}
