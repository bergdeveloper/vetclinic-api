package io.github.bergdeveloper.vetclinic.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.bergdeveloper.vetclinic.config.TenantContext;
import io.github.bergdeveloper.vetclinic.config.TenantDataSourceConfig;
import io.github.bergdeveloper.vetclinic.entity.Usuario;
import io.github.bergdeveloper.vetclinic.entity.sistema.Administrador;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Cliente;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Gestor;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Recepcionista;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Veterinario;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.repository.UsuarioRepository;
import io.github.bergdeveloper.vetclinic.service.AutenticacaoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FiltroDeSeguranca extends OncePerRequestFilter {

    @Autowired
    private List<UsuarioRepository<? extends Usuario>> usuarioRepositories;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TenantDataSourceConfig tenantDataSourceConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extrair_token_header(request);

        if (token != null) {
            try {
                String[] dados = extrairIdERole(token);

                int id_usuario = Integer.parseInt(dados[0]);
                String role = dados[1];
                Usuario usuario = null;
                if(role.equalsIgnoreCase(Role.ADMINISTRADOR_SISTEMA.name())){
                    usuario = buscarAdministradorPorId(id_usuario);
                }
                if(usuario == null){
                    usuario = buscarUsuarioTenantPorId(id_usuario, role);
                    System.out.println("TenantContext antes do filterChain: " + TenantContext.getCurrentTenant());
                    TenantContext.setCurrentTenant(determinarTenantPorCpf(usuario.getCpf()));
                    System.out.println("TenantContext depois do filterChain: " + TenantContext.getCurrentTenant());
                }else{
                    TenantContext.setCurrentTenant(determinarBancoMasterPorCpf(usuario.getCpf()));
                }

                // Cria a autenticação e define no contexto de segurança
                var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(autenticacao);

                System.out.println("Usuário autenticado: " + usuario.getCpf() + " - Role: " + usuario.getRole());
                System.out.println("Role que exige a autenticacao: " + usuario.getAuthorities());
            } catch (RuntimeException e) {
                throw new RuntimeException("Erro: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    // Extrai o token do header
    public String extrair_token_header(HttpServletRequest request) {
        var auth_header = request.getHeader("Authorization");

        if (auth_header == null || !auth_header.startsWith("Bearer ")) {
            return null;
        }

        return auth_header.substring(7); // Remove o "Bearer " do início
    }


    public String[] extrairIdERole(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token inválido: O token está vazio.");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256("senha_algoritmo_encriptacao");

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("VetClinic")
                    .build()
                    .verify(token);

            Integer id = Integer.parseInt(decodedJWT.getSubject());
            String role = decodedJWT.getClaim("role").asString();

            Map<String, Object> dados = new HashMap<>();
            dados.put("id", id);
            dados.put("role", role);

            String[] valores = new String[2];
            valores[0] = id.toString();
            valores[1] = role;
            return valores;

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido: " + exception.getMessage());
        }
    }

    public String determinarBancoMasterPorCpf(String cpf) {
        try {
            DataSource masterDataSource = tenantDataSourceConfig.getDataSourceForMasterDatabase();
            JdbcTemplate masterJdbcTemplate = new JdbcTemplate(masterDataSource);

            // Query para verificar se o CPF existe no banco master
            String sql = "SELECT cpf FROM tb_administrador WHERE cpf = ?";
            List<Map<String, Object>> results = masterJdbcTemplate.queryForList(sql, cpf);

            if (!results.isEmpty()) {
                return "master"; // Retorna "master" se o CPF for encontrado no banco master
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar CPF no banco master: " + e.getMessage());
        }

        return null; // Retorna null se o CPF não for encontrado no banco master
    }

    // Determina o tenant baseado no CPF
    public String determinarTenantPorCpf(String cpf) {
        Map<String, DataSource> tenantDataSources = tenantDataSourceConfig.getAllTenantDataSources();

        for (Map.Entry<String, DataSource> entry : tenantDataSources.entrySet()) {
            String tenantDatabase = entry.getKey();
            DataSource tenantDataSource = entry.getValue();

            try {
                JdbcTemplate tenantJdbcTemplate = new JdbcTemplate(tenantDataSource);

                // Query otimizada para buscar em várias tabelas de uma vez
                String sql = """
                            SELECT cpf FROM (
                                SELECT cpf FROM tb_gestor WHERE cpf = ? 
                                UNION ALL 
                                SELECT cpf FROM tb_veterinario WHERE cpf = ?
                                UNION ALL
                                SELECT cpf FROM tb_recepcionista WHERE cpf = ?
                                UNION ALL
                                SELECT cpf FROM tb_cliente WHERE cpf = ?
                            ) AS resultados
                        """;


                List<Map<String, Object>> results = tenantJdbcTemplate.queryForList(sql, cpf, cpf, cpf, cpf);

                if (!results.isEmpty()) {
                    return tenantDatabase; // Retorna o nome do banco correto
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar CPF no banco " + tenantDatabase + ": " + e.getMessage());
            }
        }

        return null; // Retorna null se o CPF não for encontrado em nenhum banco
    }

    public Usuario buscarAdministradorPorId(int id_usuario) {
        try {
            // Conexão ao banco de dados mestre
            DataSource masterDataSource = tenantDataSourceConfig.getDataSourceForMasterDatabase();
            JdbcTemplate masterJdbcTemplate = new JdbcTemplate(masterDataSource);

            // Consulta ao banco para verificar se o administrador existe
            String sql = "SELECT id, cpf, senha, nome, email FROM tb_administrador WHERE id = ?";
            List<Map<String, Object>> results = masterJdbcTemplate.queryForList(sql, id_usuario);

            // Verifica se o CPF foi encontrado
            if (results.isEmpty()) {
                return null; // Retorna null se não encontrar o CPF
            }

            Map<String, Object> result = results.get(0);

            // Criação e retorno do objeto Administrador autenticado
            Administrador administrador = new Administrador();


            administrador.setId((Integer) result.get("id"));
            administrador.setCpf((String) result.get("cpf"));
            administrador.setNome((String) result.get("nome"));
            administrador.setEmail((String) result.get("email"));
            administrador.setRole(Role.ADMINISTRADOR_SISTEMA); // Atribui o Role de Administrador

            return administrador; // Retorna o objeto Administrador autenticado
        } catch (Exception e) {
            System.out.println("Erro ao buscar administrador: " + e.getMessage());
            return null; // Caso ocorra um erro, retorna null
        }
    }

    public Usuario buscarUsuarioTenantPorId(int id_usuario, String role_name) {
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
                    String sql = "SELECT id, cpf, senha, nome, email FROM " + tabela_selecionada + " WHERE id = ?";
                    List<Map<String, Object>> results = tenantJdbcTemplate.queryForList(sql, id_usuario);

                    if (!results.isEmpty()) {
                        Map<String, Object> result = results.get(0);

                        Usuario usuario = null;
                        if ("tb_gestor".equals(tabela_selecionada)) {
                            usuario = new Gestor(); // Ou outro tipo de usuário
                            usuario.setRole(Role.GESTOR);
                        } else if ("tb_veterinario".equals(tabela_selecionada)) {
                            usuario = new Veterinario(); // Ou outro tipo de usuário
                            usuario.setRole(Role.VETERINARIO);
                        }else if ("tb_recepcionista".equals(tabela_selecionada)) {
                            usuario = new Recepcionista(); // Ou outro tipo de usuário
                            usuario.setRole(Role.RECEPCIONISTA);
                        }else if ("tb_cliente".equals(tabela_selecionada)) {
                            usuario = new Cliente(); // Ou outro tipo de usuário
                            usuario.setRole(Role.CLIENTE);
                        }

                        usuario.setId((Integer) result.get("id"));
                        usuario.setCpf((String) result.get("cpf"));
                        usuario.setNome((String) result.get("nome"));
                        usuario.setEmail((String) result.get("email"));

                        return usuario;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar usuário no tenant " + tenantDatabase + ": " + e.getMessage());
            }
        }

        return null; // Não encontrado
    }
}
