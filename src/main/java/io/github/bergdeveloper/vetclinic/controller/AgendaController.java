package io.github.bergdeveloper.vetclinic.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.bergdeveloper.vetclinic.dto.AgendaDTO;
import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.service.AgendaService;
import io.github.bergdeveloper.vetclinic.service.Animalservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping("/cadastrarAgendamento")
    private ResponseEntity<String> salvar(@RequestBody AgendaDTO agendaDTO) throws UnknownHostException {
        return agendaService.criarAgendamento(agendaDTO);
    }

    @GetMapping("/meusAgendamentos")
    public ResponseEntity<?> listarVeterinario(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String[] dados = extrairIdERole(token);

        int id_usuario = Integer.parseInt(dados[0]);
        String role = dados[1];
        String cpf = agendaService.buscarCpfTenantPorId(id_usuario, role);

        return agendaService.listarAgendamentoPorCpf(cpf);
    }

    public String[] extrairIdERole(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token inválido: O token está vazio.");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256("senha_algoritmo_encriptacao");

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("VetClinic")
                    .build()
                    .verify(token);

            Integer id = Integer.parseInt(decodedJWT.getSubject());
            String role = decodedJWT.getClaim("role").asString();

            Map<String, Object> dados = new HashMap<>();
            dados.put("id", id);
            dados.put("role", role);

            String[] valores = new String[2];
            valores[0] = id.toString();
            valores[1] = role;
            return valores;

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido: " + exception.getMessage());
        }
    }
}