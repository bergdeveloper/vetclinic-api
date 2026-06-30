package io.github.bergdeveloper.vetclinic.entity.vetclinic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tb_agenda")
@Getter
@Setter
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(name = "data", nullable = false)
    private Date data;

    @ManyToOne
    @JoinColumn(name = "veterinario", referencedColumnName = "cpf", nullable = false)
    private Veterinario veterinario;

}