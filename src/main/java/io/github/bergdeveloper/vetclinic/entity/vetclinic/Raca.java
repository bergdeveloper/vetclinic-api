package io.github.bergdeveloper.vetclinic.entity.vetclinic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_raca")
@Getter
@Setter
public class Raca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "especie", referencedColumnName = "id", nullable = false)
    private Especie especie;
}