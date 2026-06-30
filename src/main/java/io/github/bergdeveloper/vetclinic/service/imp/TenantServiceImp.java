package io.github.bergdeveloper.vetclinic.service.imp;

import io.github.bergdeveloper.vetclinic.dto.TenantDTO;
import io.github.bergdeveloper.vetclinic.entity.sistema.Tenant;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.repository.TenantRepository;
import io.github.bergdeveloper.vetclinic.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TenantServiceImp implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createTenant(TenantDTO tenantDTO) {
        try{

            if (tenantDTO.nomedatabase().isBlank() ||
                    tenantDTO.usernamedatabase().isBlank() ||
                    tenantDTO.passworddatabase().isBlank() ||
                    tenantDTO.emailempresa().isBlank() ||
                    tenantDTO.cpf().isBlank() ||
                    tenantDTO.nome().isBlank() ||
                    tenantDTO.senha().isBlank() ||
                    tenantDTO.telefone().isBlank() ||
                    tenantDTO.data_nascimento() == null ||
                    tenantDTO.endereco() == null ||
                    tenantDTO.endereco().getRua().isBlank() ||
                    tenantDTO.endereco().getBairro().isBlank() ||
                    tenantDTO.endereco().getCidade().isBlank() ||
                    tenantDTO.endereco().getEstado().isBlank()) {

                throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos.");
            }

            jdbcTemplate.execute("DO $$ BEGIN IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '" + tenantDTO.usernamedatabase() + "') THEN CREATE ROLE " + tenantDTO.usernamedatabase() + " WITH LOGIN PASSWORD '" + tenantDTO.passworddatabase() + "'; ALTER ROLE " + tenantDTO.usernamedatabase() + " CREATEDB; END IF; END $$;");

            jdbcTemplate.execute("CREATE DATABASE " + tenantDTO.nomedatabase() + " OWNER " + tenantDTO.usernamedatabase());

            JdbcTemplate adminJdbcTemplate = new JdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));
            adminJdbcTemplate.execute("GRANT ALL PRIVILEGES ON DATABASE " + tenantDTO.nomedatabase() + " TO " + tenantDTO.usernamedatabase() + ";");
            adminJdbcTemplate.execute("GRANT ALL PRIVILEGES ON SCHEMA public TO " + tenantDTO.usernamedatabase() + ";");

            // Criando um novo DataSource para se conectar ao banco do tenant
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/" + tenantDTO.nomedatabase());
            dataSource.setUsername(tenantDTO.usernamedatabase());
            dataSource.setPassword(tenantDTO.passworddatabase());

            // Criando um novo JdbcTemplate para o banco do tenant
            JdbcTemplate tenantJdbcTemplate = new JdbcTemplate(dataSource);

            // Criando as tabelas no banco do tenant
            criarTabelasTenant(tenantJdbcTemplate);

            Tenant tenant = new Tenant();
            tenant.setNomedatabase(tenantDTO.nomedatabase());
            tenant.setUsernamedatabase(tenantDTO.usernamedatabase());
            tenant.setPassworddatabase(tenantDTO.passworddatabase());
            tenant.setEmail(tenantDTO.emailempresa());
            tenant.setCpf(tenantDTO.cpf());
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();


            tenantRepository.save(tenant);
            // Salvar o endereço primeiro e recuperar o ID gerado
            String insertEndereco = String.format(
                    "INSERT INTO tb_endereco (rua, numero, bairro, cidade, estado, cep, complemento) " +
                            "VALUES ('%s', %d, '%s', '%s', '%s', %d, '%s') RETURNING id;",
                    tenantDTO.endereco().getRua(),
                    tenantDTO.endereco().getNumero(),
                    tenantDTO.endereco().getBairro(),
                    tenantDTO.endereco().getCidade(),
                    tenantDTO.endereco().getEstado(),
                    tenantDTO.endereco().getCep(),
                    tenantDTO.endereco().getComplemento()
            );

            Long enderecoId = tenantJdbcTemplate.queryForObject(insertEndereco, Long.class);

            String insertGestor = "INSERT INTO tb_gestor (cpf, nome, email, senha, telefone, endereco, datanascimento, datacadastro, role, ip) " +
                    "VALUES ('" + tenantDTO.cpf() + "', '" +
                    tenantDTO.nome() + "', '" +
                    tenantDTO.emailempresa() + "', '" +
                    passwordEncoder.encode(tenantDTO.senha()) + "', '" +
                    tenantDTO.telefone() + "', " +
                    enderecoId + ", '" +
                    tenantDTO.data_nascimento() + "', CURRENT_TIMESTAMP, " +
                    Role.GESTOR.ordinal() + ", '" +
                    ip + "') ON CONFLICT (cpf) DO NOTHING;";

            tenantJdbcTemplate.execute(insertGestor);
        }catch (DataAccessException e) {
            throw new RuntimeException("Erro ao executar comandos no banco: " + e.getRootCause().getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro de validação: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void criarTabelasTenant(JdbcTemplate tenantJdbcTemplate) {
        String[] sqlStatements = {
                "CREATE TABLE IF NOT EXISTS tb_endereco (" +
                        "id SERIAL PRIMARY KEY, " +
                        "rua VARCHAR(100) NOT NULL, " +
                        "numero INT NOT NULL, " +
                        "bairro VARCHAR(100) NOT NULL, " +
                        "cidade VARCHAR(15) NOT NULL, " +
                        "estado VARCHAR(15) NOT NULL, " +
                        "cep INT NOT NULL, " +
                        "complemento VARCHAR(15) NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_cliente (" +
                        "id SERIAL PRIMARY KEY, " +
                        "cpf VARCHAR(11) UNIQUE, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "telefone VARCHAR(15) NOT NULL, " +
                        "endereco INT REFERENCES tb_endereco(id) ON DELETE CASCADE, " +
                        "data_nascimento DATE NOT NULL, " +
                        "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "role smallint check (role between 0 and 6)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_gestor(" +
                        "id SERIAL PRIMARY KEY, " +
                        "cpf VARCHAR(11) UNIQUE, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "senha VARCHAR(100) NOT NULL, " +
                        "telefone VARCHAR(15) NOT NULL, " +
                        "endereco INT REFERENCES tb_endereco(id) ON DELETE CASCADE, " +
                        "datanascimento DATE NOT NULL, " +
                        "datacadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "role SMALLINT CHECK (role >= 0 AND role <= 6), " +
                        "ip VARCHAR(50) UNIQUE NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_veterinario (" +
                        "id SERIAL PRIMARY KEY, " +
                        "cpf VARCHAR(11) UNIQUE, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "senha VARCHAR(100) NOT NULL, " +
                        "telefone VARCHAR(15) NOT NULL, " +
                        "endereco INT REFERENCES tb_endereco(id) ON DELETE CASCADE, " +
                        "datanascimento DATE NOT NULL, " +
                        "datacadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "role smallint check (role between 0 and 6), " +
                        "crmv VARCHAR(20) UNIQUE NOT NULL, " +
                        "especialidade VARCHAR(100) NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_recepcionista (" +
                        "id SERIAL PRIMARY KEY, " +
                        "cpf VARCHAR(11) UNIQUE, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "senha VARCHAR(100) NOT NULL, " +
                        "telefone VARCHAR(15) NOT NULL, " +
                        "endereco INT REFERENCES tb_endereco(id) ON DELETE CASCADE, " +
                        "data_nascimento DATE NOT NULL, " +
                        "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "role smallint check (role between 0 and 6), " +
                        "salario DOUBLE PRECISION NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_estoquista (" +
                        "id SERIAL PRIMARY KEY, " +
                        "cpf VARCHAR(11) UNIQUE, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "senha VARCHAR(100) NOT NULL, " +
                        "telefone VARCHAR(15) NOT NULL, " +
                        "endereco INT REFERENCES tb_endereco(id) ON DELETE CASCADE, " +
                        "data_nascimento DATE NOT NULL, " +
                        "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "role smallint check (role between 0 and 6), " +
                        "salario DOUBLE PRECISION NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_especie (" +
                        "id SERIAL PRIMARY KEY, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "descricao VARCHAR(100) NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_raca (" +
                        "id SERIAL PRIMARY KEY, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "descricao VARCHAR(100) NOT NULL, " +
                        "especie INT REFERENCES tb_especie(id) ON DELETE CASCADE" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_animal (" +
                        "id SERIAL PRIMARY KEY, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "especie INT REFERENCES tb_especie(id) ON DELETE CASCADE, " +
                        "raca INT REFERENCES tb_raca(id) ON DELETE CASCADE, " +
                        "datanascimento DATE NOT NULL, " +
                        "sexo VARCHAR(50) NOT NULL, " +
                        "esterilizacao VARCHAR(50) NOT NULL, " +
                        "cliente VARCHAR(11) REFERENCES tb_cliente(cpf) ON DELETE CASCADE" +
                        ");",

                "CREATE TABLE IF NOT EXISTS tb_agenda (" +
                        "id SERIAL PRIMARY KEY, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "descricao VARCHAR(100) NOT NULL, " +
                        "data DATE NOT NULL, " +
                        "veterinario VARCHAR(11) REFERENCES tb_veterinario(cpf) ON DELETE CASCADE" +
                        ");"
        };

        for (String sql : sqlStatements) {
            tenantJdbcTemplate.execute(sql);
        }
    }
}