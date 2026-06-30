package io.github.bergdeveloper.vetclinic.controller;



import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.dto.RacaDTO;
import io.github.bergdeveloper.vetclinic.service.Animalservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequestMapping("/api/usuario")
public class AnimalController {

    @Autowired
    private Animalservice animalservice;

    @PostMapping("/cadastrar/cliente/animal")
    private ResponseEntity<String> salvar(@RequestBody AnimalDTO animalDTO) throws UnknownHostException {
        return animalservice.criarAnimal(animalDTO);
    }
}