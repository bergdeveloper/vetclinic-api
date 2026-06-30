package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.EspecieDTO;
import io.github.bergdeveloper.vetclinic.dto.RacaDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Raca;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RacaService {
    public ResponseEntity<String> criarRaca(RacaDTO racaDTO);
    public List<RacaDTO> listarRacasPorEspecie(Long id);
}