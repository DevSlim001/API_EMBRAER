package com.slim.manual.domain.repository;

import java.util.Optional;

import com.slim.manual.domain.model.Bloco;
import com.slim.manual.domain.model.Manual;
import com.slim.manual.domain.model.Secao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SecaoRepository extends JpaRepository<Secao,Integer> {

    //query pra achar uma seção atraves do numero da seção e do codManual
    //query pra achar uma subseção atraves do numero da subsecao e do codSecao
    //query pra achar um bloco atraves do numero do bloco e do codSeção ou codSubSeçao
    
    //@Query("SELECT s FROM secao s INNER JOIN manual m on s.codManual=m.codManual WHERE s.numSecao = ?2 and m.codManual = ?1")
    //Secao findSecaoByNumSecaoAndCodManual(Integer codManual,String numSecao); 
    Optional<Secao> findByNumSecaoAndManual(String numSecao, Manual manual);

}
