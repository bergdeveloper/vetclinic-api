package io.github.bergdeveloper.vetclinic.service.imp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.dto.AuthDTO;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AutenticacaoServiceImp implements AutenticacaoService {

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String obter_token(AuthDTO authDTO) {
        System.out.println("🔹 Iniciando autenticação...");

        String[] administrador = buscarAdministradorPorCpf(authDTO.cpf(), authDTO.senha());

        if (administrador != null) {
            System.out.println("🔹 Admin autenticado, gerando token...");
            return gerar_token_Jwt(administrador[0], administrador[1]);
        } else {
            System.out.println("🔹 Administrador não encontrado, buscando usuário no tenant...");

            String[] usuarios = buscarUsuarioTenantPorCpf(authDTO.cpf(), authDTO.senha());

            if (buscarUsuarioTenantPorCpf(authDTO.cpf(), authDTO.senha()) != null) {
                System.out.println("🔹 Usuário do tenant autenticado, gerando token...");
                return gerar_token_Jwt(usuarios[0], usuarios[1]);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CPF ou Senha incorreta.");
            }
        }
    }

    public String[] buscarAdministradorPorCpf(String cpf, String senha) {
        try {
            // Conexão ao banco de dados mestre
            DataSource masterDataSource = tenantDataSourceConfig.getDataSourceForMasterDatabase();
            JdbcTemplate masterJdbcTemplate = new JdbcTemplate(masterDataSource);

            // Consulta ao banco para verificar se o administrador existe
            String sql = "SELECT id, cpf, senha, role FROM tb_administrador WHERE cpf = ?";
            List<Map<String, Object>> results = masterJdbcTemplate.queryForList(sql, cpf);

            // Verifica se o CPF foi encontrado
            if (results.isEmpty()) {
                return null;
            }

            Map<String, Object> result = results.get(0);
            String senhaCriptografada = (String) result.get("senha");
            int roleOrdinal = (int) result.get("role");
            Role role = Role.values()[roleOrdinal]; // Converte ordinal para enum

            Integer id = (Integer) result.get("id");

            // Verificação da senha utilizando o PasswordEncoder
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(senha, senhaCriptografada)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta.");
            }

            String [] valores = new String[2];
            valores[0] = id.toString();
            valores[1] = role.name();

            return valores;
        } catch (Exception e) {
            System.out.println("Erro ao buscar administrador: " + e.getMessage());
            return null; // Caso ocorra um erro, retorna null
        }
    }

    public String[] buscarUsuarioTenantPorCpf(String cpf, String senha) {
        Map<String, DataSource> tenantDataSources = tenantDataSourceConfig.getAllTenantDataSources();

        for (Map.Entry<String, DataSource> entry : tenantDataSources.entrySet()) {
            String tenantDatabase = entry.getKey();
            DataSource tenantDataSource = entry.getValue();

            try {
                JdbcTemplate tenantJdbcTemplate = new JdbcTemplate(tenantDataSource);
                String[] tabelasUsuarios = {"tb_gestor", "tb_veterinario", "tb_recepcionista", "tb_cliente"};

                for (String tabela : tabelasUsuarios) {
                    String sql = "SELECT id, cpf, senha, role FROM " + tabela + " WHERE cpf = ?";
                    List<Map<String, Object>> results = tenantJdbcTemplate.queryForList(sql, cpf);

                    if (!results.isEmpty()) {
                        Map<String, Object> result = results.get(0);
                        String senhaCriptografada = (String) result.get("senha");

                        int roleOrdinal = (int) result.get("role");
                        Role role = Role.values()[roleOrdinal]; // Converte ordinal para enum

                        Integer id = (Integer) result.get("id");

                        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        if (!passwordEncoder.matches(senha, senhaCriptografada)) {
                            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta.");
                        }

                        String[] valores = new String[2];
                        valores[0] = id.toString();
                        valores[1] = role.name();

                        return valores;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar usuário no tenant " + tenantDatabase + ": " + e.getMessage());
            }
        }

        return null; // Não encontrado
    }

    private String gerar_token_Jwt(String id_usuario, String role) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("senha_algoritmo_encriptacao");

            return JWT.create()
                    .withIssuer("VetClinic")
                    .withSubject(id_usuario)
                    .withClaim("role", role) // Adiciona o papel do usuário aqui corretamente
                    .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao tentar gerar o token. " + exception.getMessage());
        }
    }
}