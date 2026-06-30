package io.github.bergdeveloper.vetclinic.dto;

public record EspecieDTO(
        Long id,  // Pode ser null ao criar um novo
        String nome,
        String descricao
) {
    public EspecieDTO(String nome, String descricao) {
        this(null, nome, descricao); // Define id como null por padrão
    }
}