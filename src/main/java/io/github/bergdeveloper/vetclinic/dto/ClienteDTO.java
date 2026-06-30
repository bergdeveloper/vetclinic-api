package io.github.bergdeveloper.vetclinic.dto;


import io.github.bergdeveloper.vetclinic.entity.Endereco;

import java.util.Date;

public record ClienteDTO(
        String cpf,
        String nome,
        String email,
        String telefone,
        Endereco endereco,
        Date data_nascimento
) {
}
