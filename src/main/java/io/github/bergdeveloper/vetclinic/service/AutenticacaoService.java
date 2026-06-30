package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.AuthDTO;
import io.github.bergdeveloper.vetclinic.entity.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AutenticacaoService {
    public String obter_token(AuthDTO authDTO);
}