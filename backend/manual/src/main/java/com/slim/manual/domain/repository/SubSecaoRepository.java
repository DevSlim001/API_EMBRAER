package com.slim.manual.domain.repository;

import java.util.Optional;

import com.slim.manual.domain.model.Bloco;
import com.slim.manual.domain.model.Secao;
import com.slim.manual.domain.model.SubSecao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubSecaoRepository extends JpaRepository<SubSecao,Integer> {
    Optional<SubSecao> findByNumSubSecaoAndSecao(String numSubSecao, Secao secao);
}
