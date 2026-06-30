package io.github.bergdeveloper.vetclinic.service.imp;


import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.AnimalDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Animal;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.repository.AnimalRepository;
import io.github.bergdeveloper.vetclinic.repository.ClienteRepository;
import io.github.bergdeveloper.vetclinic.service.Animalservice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class AnimalServiceImp implements Animalservice {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Override
    public ResponseEntity<String> criarAnimal(AnimalDTO animalDTO){
        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            return new ResponseEntity<>("Erro: Tenant não definido. Faça login novamente.", HttpStatus.UNAUTHORIZED);
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            String sqlAnimalInsert = "INSERT INTO tb_animal (nome, especie, raca, datanascimento, sexo, esterilizacao, cliente) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sqlAnimalInsert,
                    animalDTO.nome(),
                    animalDTO.especie(),
                    animalDTO.raca(),
                    animalDTO.datanascimento(), // certifique-se que isso seja do tipo Date ou String formatada corretamente
                    animalDTO.sexo(),
                    animalDTO.esterilizacao(),
                    animalDTO.cliente()
            );

            return new ResponseEntity<>("Animal criado com sucesso.", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("erro: " + e.getMessage());
            return new ResponseEntity<>("Erro ao criar animal: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
