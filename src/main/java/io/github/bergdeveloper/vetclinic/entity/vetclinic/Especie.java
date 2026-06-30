package io.github.bergdeveloper.vetclinic.entity.vetclinic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tb_especie")
@Getter
@Setter
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String descricao;

    @OneToMany(mappedBy = "especie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Raca> racas;
}