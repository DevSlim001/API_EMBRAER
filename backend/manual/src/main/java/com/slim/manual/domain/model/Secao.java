package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "secao")
@Data @NoArgsConstructor @AllArgsConstructor
public class Secao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codSecao;

    @Column
    private String numSecao;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SubSecao> subSecoes = new ArrayList<SubSecao>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Bloco> blocos = new ArrayList<Bloco>();
}
