package io.github.bergdeveloper.vetclinic.service.imp;


import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.EspecieDTO;
import io.github.bergdeveloper.vetclinic.dto.RacaDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Raca;
import io.github.bergdeveloper.vetclinic.repository.AnimalRepository;
import io.github.bergdeveloper.vetclinic.repository.ClienteRepository;
import io.github.bergdeveloper.vetclinic.service.Especieservice;
import io.github.bergdeveloper.vetclinic.service.RacaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RacaServiceImp implements RacaService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Override
    public ResponseEntity<String> criarRaca(RacaDTO racaDTO) {
        String tenantAtual = TenantContext.getCurrentTenant();
        if (tenantAtual == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: Tenant não definido. Faça login novamente.");
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            // Verifica se a tabela de espécies tem registros
            String sqlCount = "SELECT COUNT(*) FROM tb_especie";
            Integer count = jdbcTemplate.queryForObject(sqlCount, Integer.class);

            if (count == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erro: Nenhuma espécie cadastrada. A tabela está vazia.");
            }

            // Query correta e segura
            String sqlRacaInsert = "INSERT INTO tb_raca (nome, descricao, especie) VALUES (?, ?, ?)";
            System.out.println("Especie id que está sento insertado: " + racaDTO.especie());
            jdbcTemplate.update(sqlRacaInsert, racaDTO.nome(), racaDTO.descricao(), racaDTO.especie());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Raca criada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao criar raca: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar raca: " + e.getMessage());
        }
    }

    @Override
    public List<RacaDTO> listarRacasPorEspecie(Long id) {
        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            throw new RuntimeException("Erro: Tenant não definido. Faça login novamente.");
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            // Verifica se existe essa espécie
            String sqlCheckEspecie = "SELECT COUNT(*) FROM tb_especie WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(sqlCheckEspecie, Integer.class, id);

            if (count == null || count == 0) {
                throw new RuntimeException("Espécie com ID " + id + " não encontrada.");
            }

            // Corrigido: passando o id como argumento da query
            String sql = "SELECT id, nome, descricao, especie FROM tb_raca WHERE especie = ?";
            return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                    new RacaDTO(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getInt("especie")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar raças: " + e.getMessage());
        }
    }
}
