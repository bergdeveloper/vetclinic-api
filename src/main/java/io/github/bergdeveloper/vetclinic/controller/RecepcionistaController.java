package io.github.bergdeveloper.vetclinic.controller;

import io.github.bergdeveloper.vetclinic.dto.ClienteDTO;
import io.github.bergdeveloper.vetclinic.dto.RecepcionistaDTO;
import io.github.bergdeveloper.vetclinic.service.GestorService;
import io.github.bergdeveloper.vetclinic.service.RecepcionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recepcionista")
public class RecepcionistaController {

    @Autowired
    private RecepcionistaService recepcionistaService;

    @GetMapping("/verclientes")
    public ResponseEntity<?> listarGestores() {
        return recepcionistaService.listarClientes();
    }

    @PostMapping("/cadastrar/cliente")
    public ResponseEntity<Map<String, String>> criar_cliente(@RequestBody ClienteDTO clienteDTO) {
        recepcionistaService.criar_cliente(clienteDTO);

        // Criando um JSON de resposta com os dados cadastrados
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Recepcionista cadastrado com sucesso.");
        response.put("nome", clienteDTO.nome());
        response.put("cpf", clienteDTO.cpf());
        response.put("email", clienteDTO.email());

        return ResponseEntity.ok(response);
    }
}