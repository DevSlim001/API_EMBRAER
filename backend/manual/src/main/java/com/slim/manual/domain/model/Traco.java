package com.slim.manual.domain.model;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "traco")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Traco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTraco;

    @Column
    private String nome;
    
    @Column
    private Integer traco;

    @JsonIgnore
    @ManyToMany(mappedBy = "tracos")
    List<Bloco> blocos;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="codManual")
    private Manual manual; 

    public void removeBloco(Bloco bloco){
        this.blocos.remove(bloco);
        bloco.removeTraco(this);
    }
    
}

/* 
package com.slim.manual.domain.model;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "traco")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Traco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTraco;

    @Column
    private String nome;
    
    @Column
    private Integer traco;

    @ManyToMany(mappedBy = "tracos")
    List<CodelistLinha> codelistLinha;

    
}
*/