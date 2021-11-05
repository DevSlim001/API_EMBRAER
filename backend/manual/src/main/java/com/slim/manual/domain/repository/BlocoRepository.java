package com.slim.manual.domain.repository;

import java.util.List;
import java.util.Optional;

import com.slim.manual.domain.model.Arquivo;
import com.slim.manual.domain.model.Bloco;
import com.slim.manual.domain.model.Secao;
import com.slim.manual.domain.model.SubSecao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlocoRepository extends JpaRepository<Bloco,Integer> {
    Optional<List<Bloco>> findByNumBlocoAndSecao(String numBloco, Secao secao);
    Optional<List<Bloco>> findByNumBlocoAndSubSecao(String numBloco, SubSecao subSecao);
    Optional<Bloco> findByArquivo(Arquivo arquivo);

}
