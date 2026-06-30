package io.github.bergdeveloper.vetclinic.service.imp;


import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.AgendaDTO;
import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.entity.Usuario;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Cliente;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Gestor;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Recepcionista;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Veterinario;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.repository.AnimalRepository;
import io.github.bergdeveloper.vetclinic.repository.ClienteRepository;
import io.github.bergdeveloper.vetclinic.service.AgendaService;
import io.github.bergdeveloper.vetclinic.service.Animalservice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgendaServiceImp implements AgendaService {

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Override
    public ResponseEntity<String> criarAgendamento(AgendaDTO agendaDTO){
        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            return new ResponseEntity<>("Erro: Tenant não definido. Faça login novamente.", HttpStatus.UNAUTHORIZED);
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            String[] tabelasUsuarios = {"tb_veterinario"}; // posso adicionar mais tb, porém preciso modificar na tabela agenda e acrescentar novos usuarios.

            Integer count = null;
            for(String tabela : tabelasUsuarios){

                String sqlCpf = "SELECT COUNT(*) FROM " + tabela + " WHERE cpf = ?";

                count = jdbcTemplate.queryForObject(sqlCpf, Integer.class, agendaDTO.veterinario());

                if(count != null && count > 0){
                    break;
                }

            }

            if (count == null || count == 0) {
                return new ResponseEntity<>("CPF do veterinário não encontrado. Verifique se está correto e já cadastrado.", HttpStatus.BAD_REQUEST);
            }

            String sqlAnimalInsert = "INSERT INTO tb_agenda (nome, descricao, data, veterinario) " +
                    "VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(sqlAnimalInsert,
                    agendaDTO.nome(),
                    agendaDTO.descricao(),
                    agendaDTO.data(),
                    agendaDTO.veterinario()
            );

            return new ResponseEntity<>("Agendamento criado com sucesso.", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("erro: " + e.getMessage());
            return new ResponseEntity<>("Erro ao criar agendamento: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> listarAgendamentoPorCpf(String cpf) {
        System.out.println("Verificando TenantContext antes de acessar: " + TenantContext.getCurrentTenant());

        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: Tenant não definido. Faça login novamente.");
        }

        System.out.println("TenantAtual após verificar: " + tenantAtual);  // Verificação do tenant atual

        try {
            // Obtém o DataSource correspondente ao tenant
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            // Query para listar os agendamentos do veterinário pelo CPF
            String sql = "SELECT nome, descricao, data FROM tb_agenda WHERE veterinario = ?";
            List<Map<String, Object>> agendamentos = jdbcTemplate.queryForList(sql, cpf);

            if (agendamentos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Nenhum agendamento encontrado para o CPF: " + cpf);
            }

            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            System.out.println("Erro ao buscar agendamentos: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar agendamentos: " + e.getMessage());
        }
    }

    @Override
    public String buscarCpfTenantPorId(int id_usuario, String role_name) {
        Map<String, DataSource> tenantDataSources = tenantDataSourceConfig.getAllTenantDataSources();

        for (Map.Entry<String, DataSource> entry : tenantDataSources.entrySet()) {
            String tenantDatabase = entry.getKey();
            DataSource tenantDataSource = entry.getValue();

            try {
                JdbcTemplate tenantJdbcTemplate = new JdbcTemplate(tenantDataSource);
                String tabela_selecionada = null;
                String[] tabelasUsuarios = {"tb_gestor", "tb_veterinario", "tb_recepcionista", "tb_cliente"};
                if(role_name.equalsIgnoreCase(Role.GESTOR.name())){
                    tabela_selecionada = tabelasUsuarios[0];
                }else if(role_name.equalsIgnoreCase(Role.VETERINARIO.name())){
                    tabela_selecionada = tabelasUsuarios[1];
                }else if(role_name.equalsIgnoreCase(Role.RECEPCIONISTA.name())){
                    tabela_selecionada = tabelasUsuarios[2];
                }else if(role_name.equalsIgnoreCase(Role.CLIENTE.name())){
                    tabela_selecionada = tabelasUsuarios[3];
                }

                if(tabela_selecionada != null){
                    String sql = "SELECT cpf FROM " + tabela_selecionada + " WHERE id = ?";
                    List<Map<String, Object>> results = tenantJdbcTemplate.queryForList(sql, id_usuario);

                    if (!results.isEmpty()) {
                        Map<String, Object> result = results.get(0);

                        return (String) result.get("cpf");
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar usuário no tenant " + tenantDatabase + ": " + e.getMessage());
            }
        }

        return null; // Não encontrado
    }
}
