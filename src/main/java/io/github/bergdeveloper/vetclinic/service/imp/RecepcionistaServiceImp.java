package io.github.bergdeveloper.vetclinic.service.imp;

import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.ClienteDTO;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.service.RecepcionistaService;
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
public class RecepcionistaServiceImp implements RecepcionistaService {

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;


    @Override
    public ResponseEntity<?> listarClientes() {
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
            String sql = "SELECT cpf, nome, email FROM tb_cliente";
            List<Map<String, Object>> clientes = jdbcTemplate.queryForList(sql);

            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            System.out.println("gwegwegwegwe " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar gestores: " + e.getMessage());
        }
    }

    @Override
    public void criar_cliente(ClienteDTO clienteDTO) {
        String tenantAtual = TenantContext.getCurrentTenant(); // Obtém o nome do tenant
        if (tenantAtual == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: Tenant não definido. Faça login novamente.");
            return;
        }

        try {
            DataSource tenantDataSource = tenantDataSourceConfig.getDataSourceForTenant(tenantAtual);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);

            // Inserir o endereço do cliente
            String insertEndereco = String.format(
                    "INSERT INTO tb_endereco (rua, numero, bairro, cidade, estado, cep, complemento) " +
                            "VALUES ('%s', %d, '%s', '%s', '%s', %d, '%s') RETURNING id;",
                    clienteDTO.endereco().getRua(),
                    clienteDTO.endereco().getNumero(),
                    clienteDTO.endereco().getBairro(),
                    clienteDTO.endereco().getCidade(),
                    clienteDTO.endereco().getEstado(),
                    clienteDTO.endereco().getCep(),
                    clienteDTO.endereco().getComplemento()
            );

            Long enderecoId = jdbcTemplate.queryForObject(insertEndereco, Long.class);

            // Inserir o cliente na tabela tb_cliente com o ID do endereço
            String sqlClienteInsert = "INSERT INTO tb_cliente (cpf, nome, email, telefone, endereco, data_nascimento, role) VALUES ('" +
                    clienteDTO.cpf() + "', '" +
                    clienteDTO.nome() + "', '" +
                    clienteDTO.email() + "', '" +
                    clienteDTO.telefone() + "', " +
                    enderecoId + ", '" +
                    clienteDTO.data_nascimento() + "', " +
                    Role.CLIENTE.ordinal() + ")";

            jdbcTemplate.execute(sqlClienteInsert);

            ResponseEntity.status(HttpStatus.CREATED)
                    .body("Cliente cadastrado com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }
}
