package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.ClienteDTO;
import org.springframework.http.ResponseEntity;

public interface RecepcionistaService {
    public void criar_cliente(ClienteDTO clienteDTO);
    public ResponseEntity<?> listarClientes();
}
