package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.slim.manual.rest.dto.ManualDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "manual")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Manual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codManual;
    
    @Column
    private String partNumber;
    
    @Column
    private String nome;

    @OneToMany(mappedBy = "manual")
    private List<Secao> secoes ;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "manual")
    private List<Revisao> revisoes = new ArrayList<Revisao>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "manual")
    private List<Traco> tracos = new ArrayList<Traco>();

    public ManualDTO toManualDTO(){
        return ManualDTO
                    .builder()
                    .codManual(this.codManual)
                    .partNumber(this.partNumber)
                    .nome(this.nome)
                    .build();
    } 

}

