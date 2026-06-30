package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.ClienteDTO;
import io.github.bergdeveloper.vetclinic.dto.TenantDTO;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.util.List;

public interface TenantService {
    public void createTenant(TenantDTO tenantDTO);
}