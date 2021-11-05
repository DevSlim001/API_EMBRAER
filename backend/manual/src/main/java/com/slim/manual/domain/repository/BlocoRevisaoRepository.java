package com.slim.manual.domain.repository;

import java.util.List;
import java.util.Optional;

import com.slim.manual.domain.model.Arquivo;
import com.slim.manual.domain.model.BlocoRevisao;
import com.slim.manual.domain.model.Revisao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlocoRevisaoRepository extends JpaRepository<BlocoRevisao,Integer> {
    List<BlocoRevisao> findByRevisaoOrderByBloco(Revisao revisao);
}
