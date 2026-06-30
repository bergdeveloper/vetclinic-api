package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.RecepcionistaDTO;
import io.github.bergdeveloper.vetclinic.dto.VeterinarioDTO;
import org.springframework.http.ResponseEntity;

public interface GestorService {
    public ResponseEntity<?> listarGestores();
    public void criarRecepcionista(RecepcionistaDTO recepcionistaDTO);
    public void criarVeterinario(VeterinarioDTO veterinarioDTO);
}
