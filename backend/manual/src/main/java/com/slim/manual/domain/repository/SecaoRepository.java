package com.slim.manual.domain.repository;

import com.slim.manual.domain.model.Secao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecaoRepository extends JpaRepository<Secao,Integer> {
}
