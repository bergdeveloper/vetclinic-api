package io.github.bergdeveloper.vetclinic.controller;

import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.dto.RecepcionistaDTO;
import io.github.bergdeveloper.vetclinic.service.GestorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class LogoutController {


    @PostMapping("/logout")
    public void logout() {

        System.out.println("Context antes de limpar: " + TenantContext.getCurrentTenant());

        SecurityContextHolder.clearContext();
        // Limpa o contexto do Tenant
        TenantContext.clear();

        System.out.println("Context depois de limpar: " + TenantContext.getCurrentTenant());


    }
}