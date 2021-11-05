package com.slim.manual.domain.model;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slim.manual.rest.dto.ManualDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "bloco")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Bloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codBloco;
    
    @Column
    private String nomeBloco;
    
    @Column
    private String numBloco;

    @Column
    private String codBlocoCodelist;
  
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codArquivo", referencedColumnName = "codArquivo")
    private Arquivo arquivo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codSecao")
    private Secao secao;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codSubSecao")
    private SubSecao subSecao;

    @ManyToMany
    @JoinTable(
        name = "bloco_tracos", 
        joinColumns = @JoinColumn(name = "cod_bloco"), 
        inverseJoinColumns = @JoinColumn(name = "cod_traco")
    )
    private List<Traco> tracos;

    public void removeTraco(Traco traco){
        this.tracos.remove(traco);
        traco.getBlocos().remove(this);
    } 
}

/* 
package com.slim.manual.domain.model;

import java.util.List;

import javax.persistence.*;

import com.slim.manual.rest.dto.ManualDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "bloco")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Bloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codBloco;
    
    @Column
    private String nomeBloco;
    
    @Column
    private String numBloco;

    @Column
    private String codBlocoCodelist;

}
*/
