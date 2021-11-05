package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
@Entity(name = "bloco_revisao")
public class BlocoRevisao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codBlocoRevisao;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codRevisao")
    private Revisao revisao; 

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codArquivo", referencedColumnName = "codArquivo")
    private Arquivo arquivo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codBloco", referencedColumnName = "codBloco")
    private Bloco bloco;
}
