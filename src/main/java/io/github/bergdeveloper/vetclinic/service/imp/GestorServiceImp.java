package io.github.bergdeveloper.vetclinic.service.imp;

import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.RecepcionistaDTO;
import io.github.bergdeveloper.vetclinic.dto.VeterinarioDTO;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.service.GestorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GestorServiceImp implements GestorService {

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> listarGestores() {
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

            // Query para listar os gestores
            String sql = "SELECT cpf, nome, email FROM tb_gestor";
            List<Map<String, Object>> gestores = jdbcTemplate.queryForList(sql);

            return ResponseEntity.ok(gestores);
        } catch (Exception e) {
            System.out.println("gwegwegwegwe " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar gestores: " + e.getMessage());
        }
    }

    @Override
    public void criarRecepcionista(RecepcionistaDTO recepcionistaDTO) {

        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: Tenant não definido. Faça login novamente.");
            return;
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            String insertEndereco = String.format(
                    "INSERT INTO tb_endereco (rua, numero, bairro, cidade, estado, cep, complemento) " +
                            "VALUES ('%s', %d, '%s', '%s', '%s', %d, '%s') RETURNING id;",
                    recepcionistaDTO.endereco().getRua(),
                    recepcionistaDTO.endereco().getNumero(),
                    recepcionistaDTO.endereco().getBairro(),
                    recepcionistaDTO.endereco().getCidade(),
                    recepcionistaDTO.endereco().getEstado(),
                    recepcionistaDTO.endereco().getCep(),
                    recepcionistaDTO.endereco().getComplemento()
            );

            Long enderecoId = jdbcTemplate.queryForObject(insertEndereco, Long.class);

            String sqlRecepcionistaInsert = "INSERT INTO tb_recepcionista (cpf, nome, email, senha, telefone, endereco, " +
                    "data_nascimento, role, salario) VALUES ('" + recepcionistaDTO.cpf() + "', '" +
                    recepcionistaDTO.nome() + "', '" +
                    recepcionistaDTO.email() + "', '" +
                    passwordEncoder.encode(recepcionistaDTO.senha()) + "', '" +
                    recepcionistaDTO.telefone() + "', " +
                    enderecoId + ", '" +
                    recepcionistaDTO.data_nascimento() + "', " +
                    Role.RECEPCIONISTA.ordinal() + ", " +
                    recepcionistaDTO.salario() + ")";

            jdbcTemplate.execute(sqlRecepcionistaInsert);

            ResponseEntity.status(HttpStatus.CREATED)
                    .body("Recepcionista e endereço criados com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao criar recepcionista: " + e.getMessage());
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar recepcionista: " + e.getMessage());
        }
    }

    @Override
    public void criarVeterinario(VeterinarioDTO veterinarioDTO) {

        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: Tenant não definido. Faça login novamente.");
            return;
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            String insertEndereco = String.format(
                    "INSERT INTO tb_endereco (rua, numero, bairro, cidade, estado, cep, complemento) " +
                            "VALUES ('%s', %d, '%s', '%s', '%s', %d, '%s') RETURNING id;",
                    veterinarioDTO.endereco().getRua(),
                    veterinarioDTO.endereco().getNumero(),
                    veterinarioDTO.endereco().getBairro(),
                    veterinarioDTO.endereco().getCidade(),
                    veterinarioDTO.endereco().getEstado(),
                    veterinarioDTO.endereco().getCep(),
                    veterinarioDTO.endereco().getComplemento()
            );

            Long enderecoId = jdbcTemplate.queryForObject(insertEndereco, Long.class);

            String sqlRecepcionistaInsert = "INSERT INTO tb_veterinario (cpf, nome, email, senha, telefone, endereco, " +
                    "datanascimento, role, crmv, especialidade) VALUES ('" + veterinarioDTO.cpf() + "', '" +
                    veterinarioDTO.nome() + "', '" +
                    veterinarioDTO.email() + "', '" +
                    passwordEncoder.encode(veterinarioDTO.senha()) + "', '" +
                    veterinarioDTO.telefone() + "', " +
                    enderecoId + ", '" +
                    veterinarioDTO.datanascimento() + "', " +
                    Role.VETERINARIO.ordinal() + ", '" +
                    veterinarioDTO.crmv() + "', '" +
                    veterinarioDTO.especialidade() + "')";


            jdbcTemplate.execute(sqlRecepcionistaInsert);

            ResponseEntity.status(HttpStatus.CREATED)
                    .body("Veterinario e endereço criados com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao criar veterinario: " + e.getMessage());
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar veterinario: " + e.getMessage());
        }
    }
}
