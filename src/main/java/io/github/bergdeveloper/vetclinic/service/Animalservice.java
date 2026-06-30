package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import org.springframework.http.ResponseEntity;

public interface Animalservice {
    public ResponseEntity<String> criarAnimal(AnimalDTO animalDTO);
}