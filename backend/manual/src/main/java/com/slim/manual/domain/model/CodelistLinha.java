package com.slim.manual.domain.model;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/* 
@Builder @Data @AllArgsConstructor @NoArgsConstructor */
//@Entity(name = "codelist_linha")
public class CodelistLinha {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
/*     private Integer codCodelistLinha;
    
    @Column
    private String secao;
    
    @Column
    private String subSecao;
    
    @Column
    private String numeroBloco;
    
    @Column
    private String nomeBloco;
    
    @Column
    private String codBloco; */
    
    /* @OneToMany(cascade = CascadeType.ALL,mappedBy = "codelistLinha")
    private List<Traco> tracos; */

/*     @ManyToMany
    @JoinTable(
        name = "bloco_tracos", 
        joinColumns = @JoinColumn(name = "codCodelistLinha"), 
        inverseJoinColumns = @JoinColumn(name = "cod_traco")
    )
    private List<Traco> tracos; */
}
