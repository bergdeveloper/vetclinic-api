package io.github.bergdeveloper.vetclinic.controller;

import io.github.bergdeveloper.vetclinic.dto.AuthDTO;
import io.github.bergdeveloper.vetclinic.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class AutenticacaoController {

    @Autowired
    private final AutenticacaoService autenticacaoService;


    @PostMapping("/logar")
    public ResponseEntity<Map<String, String>> autenticar(@RequestBody AuthDTO authDTO) {

        String token = autenticacaoService.obter_token(authDTO);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}