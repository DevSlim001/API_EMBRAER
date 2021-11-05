package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "subsecao")
@Data @NoArgsConstructor @AllArgsConstructor 
public class SubSecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codSubSecao;
    
    @Column
    private String numSubSecao;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codSecao")
    private Secao secao; 

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "subSecao")
    private List<Bloco> blocos = new ArrayList<Bloco>();
}