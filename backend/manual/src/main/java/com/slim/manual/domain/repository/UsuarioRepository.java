package com.slim.manual.domain.repository;

import com.slim.manual.domain.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    
}
