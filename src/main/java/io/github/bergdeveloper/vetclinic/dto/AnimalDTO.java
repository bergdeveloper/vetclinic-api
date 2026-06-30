package io.github.bergdeveloper.vetclinic.dto;



import io.github.bergdeveloper.vetclinic.entity.vetclinic.Especie;

import java.util.Date;

public record AnimalDTO(
        String nome,
        int especie,
        int raca,
        Date datanascimento,
        String sexo,
        String esterilizacao,
        String cliente
) {
}