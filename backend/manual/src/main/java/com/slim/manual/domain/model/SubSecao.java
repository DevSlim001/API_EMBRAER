package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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

    @OneToMany(cascade = CascadeType.ALL)
    private List<Bloco> blocos = new ArrayList<Bloco>();
}

/* package com.slim.manual.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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

    @OneToMany(cascade = CascadeType.ALL)
    private List<Bloco> blocos = new ArrayList<Bloco>();
}
 */