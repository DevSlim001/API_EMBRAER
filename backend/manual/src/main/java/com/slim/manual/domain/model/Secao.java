package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "secao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Secao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codSecao;

    @Column
    private String numSecao;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codManual", nullable=false)
    private Manual manual;

    @OneToMany(mappedBy = "secao")
    private List<SubSecao> subSecoes = new ArrayList<SubSecao>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "secao")
    private List<Bloco> blocos = new ArrayList<Bloco>();


/* 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "codSecao")
    private Secao secao;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "secao")
    private List<Secao> subSecoesTeste; */
}
