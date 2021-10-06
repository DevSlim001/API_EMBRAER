package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.slim.manual.rest.dto.ManualDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "manual")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Manual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codManual;
    
    @Column
    private String partNumber;
    
    @Column
    private String nome;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Secao> secoes ;

    public ManualDTO toManualDTO(){
        return ManualDTO
                    .builder()
                    .partNumber(this.partNumber)
                    .nome(this.nome)
                    .build();
    } 

}

/* 

package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.slim.manual.rest.dto.ManualDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "manual")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Manual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codManual;
    
    @Column
    private String partNumber;
    
    @Column
    private String nome;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codCodelist", referencedColumnName = "codCodelist")
    private Codelist codelist;


}
*/
