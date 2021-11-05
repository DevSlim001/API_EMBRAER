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
@Entity(name = "revisao")
public class Revisao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codRevisao;
    
    @Column
    private String nomeRevisao;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "revisao")
    private List<BlocoRevisao> blocos = new ArrayList<BlocoRevisao>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codManual")
    private Manual manual;
}
