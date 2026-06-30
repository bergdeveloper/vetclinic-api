package io.github.bergdeveloper.vetclinic.dto;


import io.github.bergdeveloper.vetclinic.entity.Endereco;

import java.util.Date;

public record VeterinarioDTO(
        String cpf,
        String nome,
        String email,
        String senha,
        String telefone,
        Endereco endereco,
        Date datanascimento,
        String crmv,
        String especialidade
) {
}
