package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.dto.EspecieDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Especie;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface Especieservice {
    public ResponseEntity<Map<String, String>> criarEspecie(EspecieDTO especieDTO);
    public List<EspecieDTO> listarEspecies();
}