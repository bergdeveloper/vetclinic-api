package io.github.bergdeveloper.vetclinic.controller;


import io.github.bergdeveloper.vetclinic.dto.TenantDTO;
import io.github.bergdeveloper.vetclinic.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createTenant(@RequestBody TenantDTO tenantDTO) {
        tenantService.createTenant(tenantDTO);

        // Criando um JSON de resposta com os dados cadastrados
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Tenancy cadastrado com sucesso.");
        response.put("nome", tenantDTO.nome());
        response.put("cpf", tenantDTO.cpf());
        response.put("email", tenantDTO.emailempresa());

        return ResponseEntity.ok(response);
    }
}
