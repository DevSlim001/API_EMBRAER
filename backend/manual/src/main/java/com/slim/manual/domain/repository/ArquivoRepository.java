package com.slim.manual.domain.repository;

import java.util.Optional;

import com.slim.manual.domain.model.Arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository<Arquivo,Integer> {
    Optional<Arquivo> findByNomeArquivoLike(String nomeArquivo);

}
