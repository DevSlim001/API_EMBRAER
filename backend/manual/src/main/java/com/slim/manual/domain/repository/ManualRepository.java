package com.slim.manual.domain.repository;

import java.util.Optional;

import com.slim.manual.domain.model.Manual;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManualRepository extends JpaRepository<Manual,Integer> {
}
