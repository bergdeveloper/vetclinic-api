package io.github.bergdeveloper.vetclinic.entity.vetclinic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tb_animal")
@Getter
@Setter
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "especie", referencedColumnName = "id", nullable = false)
    private Especie especie;

    @ManyToOne
    @JoinColumn(name = "raca", referencedColumnName = "id", nullable = false)
    private Raca raca;

    @Column(name = "datanascimento", nullable = false)
    private Date datanascimento;

    @Column(name = "sexo", nullable = false)
    private String sexo;

    @Column(name = "esterilizacao", nullable = false)
    private String esterilizacao;

    @ManyToOne
    @JoinColumn(name = "cliente", referencedColumnName = "cpf", nullable = false)
    private Cliente cliente;
}