package io.github.bergdeveloper.vetclinic.dto;


public record RacaDTO(Long id, String nome, String descricao, int especie) {
    // Construtor não canônico precisa chamar o primário
    public RacaDTO(String nome, String descricao, int especie) {
        this(null, nome, descricao, especie); // Define id como null para criação
    }
}