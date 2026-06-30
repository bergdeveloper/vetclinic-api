package io.github.bergdeveloper.vetclinic.controller;

import io.github.bergdeveloper.vetclinic.dto.RecepcionistaDTO;
import io.github.bergdeveloper.vetclinic.dto.TenantDTO;
import io.github.bergdeveloper.vetclinic.dto.VeterinarioDTO;
import io.github.bergdeveloper.vetclinic.service.GestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gestores")
public class GestorController {

    @Autowired
    private GestorService gestorService;

    @GetMapping("/vergestores")
    public ResponseEntity<?> listarGestores() {
        return gestorService.listarGestores();
    }

    @PostMapping("/cadastrar/recepcionista")
    public ResponseEntity<Map<String, String>> criarRecepcionista(@RequestBody RecepcionistaDTO recepcionistaDTO) {
        gestorService.criarRecepcionista(recepcionistaDTO);

        // Criando um JSON de resposta com os dados cadastrados
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Recepcionista cadastrado com sucesso.");
        response.put("nome", recepcionistaDTO.nome());
        response.put("cpf", recepcionistaDTO.cpf());
        response.put("email", recepcionistaDTO.email());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastrar/veterinario")
    public ResponseEntity<Map<String, String>> criarVeterinario(@RequestBody VeterinarioDTO veterinarioDTO) {
        gestorService.criarVeterinario(veterinarioDTO);

        // Criando um JSON de resposta com os dados cadastrados
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Veterinario cadastrado com sucesso.");
        response.put("nome", veterinarioDTO.nome());
        response.put("cpf", veterinarioDTO.cpf());
        response.put("email", veterinarioDTO.email());

        return ResponseEntity.ok(response);
    }
}