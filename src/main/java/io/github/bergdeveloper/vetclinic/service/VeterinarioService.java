package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.VeterinarioDTO;
import org.springframework.http.ResponseEntity;

public interface VeterinarioService {
    public ResponseEntity<String> salvar(VeterinarioDTO veterinarioDTO);
}