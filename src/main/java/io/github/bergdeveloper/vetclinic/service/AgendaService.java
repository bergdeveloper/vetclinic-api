package io.github.bergdeveloper.vetclinic.service;

import io.github.bergdeveloper.vetclinic.dto.AgendaDTO;
import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import org.springframework.http.ResponseEntity;

public interface AgendaService {
    public ResponseEntity<String> criarAgendamento(AgendaDTO agendaDTO);
    public ResponseEntity<?> listarAgendamentoPorCpf(String cpf);
    public String buscarCpfTenantPorId(int id_usuario, String role_name);
}