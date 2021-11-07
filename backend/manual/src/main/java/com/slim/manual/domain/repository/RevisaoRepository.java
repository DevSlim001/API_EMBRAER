package com.slim.manual.domain.repository;


import java.util.List;
import java.util.Optional;

import com.slim.manual.domain.model.Manual;
import com.slim.manual.domain.model.Revisao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisaoRepository extends JpaRepository<Revisao,Integer> {
    List<Revisao> findByManual(Manual manual);
    Optional<Revisao> findByNomeRevisaoAndManual(String nomeRevisao,Manual manual);
}
