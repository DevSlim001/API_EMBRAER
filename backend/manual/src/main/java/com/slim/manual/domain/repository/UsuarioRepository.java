package com.slim.manual.domain.repository;

import java.util.Optional;

import com.slim.manual.domain.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    Optional<Usuario> findByEmail(String email);
}
