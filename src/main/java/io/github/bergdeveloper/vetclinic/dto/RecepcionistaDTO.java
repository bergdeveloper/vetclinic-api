package io.github.bergdeveloper.vetclinic.dto;


import io.github.bergdeveloper.vetclinic.entity.Endereco;

import java.util.Date;

public record RecepcionistaDTO(
        String cpf,
        String nome,
        String email,
        String senha,
        String telefone,
        Endereco endereco,
        Date data_nascimento,
        double salario
) {
}
