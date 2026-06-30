package io.github.bergdeveloper.vetclinic.controller;



import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.dto.EspecieDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Especie;
import io.github.bergdeveloper.vetclinic.service.Animalservice;
import io.github.bergdeveloper.vetclinic.service.Especieservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/especie")
public class EspecieController {

    @Autowired
    private Especieservice especieservice;

    @PostMapping("/cadastrar/especie")
    private ResponseEntity<Map<String, String>> salvar(@RequestBody EspecieDTO especieDTO) {
        return especieservice.criarEspecie(especieDTO);
    }

    @GetMapping
    public ResponseEntity<List<EspecieDTO>> listarEspecies() {
        return ResponseEntity.ok(especieservice.listarEspecies());
    }
}