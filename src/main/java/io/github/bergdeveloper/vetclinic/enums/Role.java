package io.github.bergdeveloper.vetclinic.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMINISTRADOR_SISTEMA("admin_sistema"),
    GERENTE_SISTEMA("gerente_sistema"),
    GESTOR("gestor_vetclinic"),
    VETERINARIO("veterinario_vetclinic"),
    RECEPCIONISTA("recepcionista_vetclinic"),
    ESTOQUISTA("estoquista_vetclinic"),
    CLIENTE("cliente_vetclinic");

    private String role;

    Role(String role){
        this.role = role;
    }
}
