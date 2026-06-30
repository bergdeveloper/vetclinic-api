package io.github.bergdeveloper.vetclinic.service.imp;


import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.dto.EspecieDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Especie;
import io.github.bergdeveloper.vetclinic.repository.AnimalRepository;
import io.github.bergdeveloper.vetclinic.repository.ClienteRepository;
import io.github.bergdeveloper.vetclinic.service.Animalservice;
import io.github.bergdeveloper.vetclinic.service.Especieservice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EspecieServiceImp implements Especieservice {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Override
    public ResponseEntity<Map<String, String>> criarEspecie(EspecieDTO especieDTO){
        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("mensagem", "Erro: Tenant não definido. Faça login novamente."));
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            String sqlEspecieInsert = "INSERT INTO tb_especie (nome, descricao) VALUES (?, ?)";
            jdbcTemplate.update(sqlEspecieInsert, especieDTO.nome(), especieDTO.descricao());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("mensagem", "Especie criada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("mensagem", "Erro ao criar especie."));
        }
    }

    @Override
    public List<EspecieDTO> listarEspecies() {
        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            throw new RuntimeException("Erro: Tenant não definido. Faça login novamente.");
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            String sql = "SELECT id, nome, descricao FROM tb_especie";
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new EspecieDTO(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("descricao")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar espécies: " + e.getMessage());
        }
    }
}
