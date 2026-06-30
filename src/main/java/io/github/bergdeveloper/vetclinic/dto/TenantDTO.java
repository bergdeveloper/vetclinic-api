package io.github.bergdeveloper.vetclinic.dto;


import io.github.bergdeveloper.vetclinic.entity.Endereco;

import java.util.Date;

public record TenantDTO(
        String nomedatabase,
        String usernamedatabase,
        String passworddatabase,
        String emailempresa,
        String cpf,
        String nome,
        String senha,
        String telefone,
        Date data_nascimento,
        Endereco endereco
) {
}
