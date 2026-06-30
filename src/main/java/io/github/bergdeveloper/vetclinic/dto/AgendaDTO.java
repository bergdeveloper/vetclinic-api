package io.github.bergdeveloper.vetclinic.dto;



import java.util.Date;

public record AgendaDTO(
        String nome,
        String descricao,
        Date data,
        String veterinario
) {
}