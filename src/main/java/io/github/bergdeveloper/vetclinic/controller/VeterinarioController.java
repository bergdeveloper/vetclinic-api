package io.github.bergdeveloper.vetclinic.controller;


import io.github.bergdeveloper.vetclinic.dto.VeterinarioDTO;
import io.github.bergdeveloper.vetclinic.service.VeterinarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/usuario")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @PostMapping("/cadastrar/veterinario")
    private ResponseEntity<String> salvar(@RequestBody VeterinarioDTO veterinarioDTO) {
        return veterinarioService.salvar(veterinarioDTO);
    }
}