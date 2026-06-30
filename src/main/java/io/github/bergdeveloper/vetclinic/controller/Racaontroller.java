package io.github.bergdeveloper.vetclinic.controller;



import io.github.bergdeveloper.vetclinic.dto.EspecieDTO;
import io.github.bergdeveloper.vetclinic.dto.RacaDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Raca;
import io.github.bergdeveloper.vetclinic.service.Especieservice;
import io.github.bergdeveloper.vetclinic.service.RacaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequestMapping("/api/raca")
public class Racaontroller {

    @Autowired
    private RacaService racaService;

    @PostMapping("/cadastrar/raca")
    private ResponseEntity<String> salvar(@RequestBody RacaDTO racaDTO) throws UnknownHostException {
        return racaService.criarRaca(racaDTO);
    }

    @GetMapping("/especie/{id}")
    public ResponseEntity<List<RacaDTO>> listarRacasPorEspecie(@PathVariable Long id) {
        List<RacaDTO> racas = racaService.listarRacasPorEspecie(id);
        return ResponseEntity.ok(racas);
    }
}