package io.github.bergdeveloper.vetclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TenantDataSourceConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, DataSource> getAllTenantDataSources() {
        String sql = "SELECT nomedatabase, usernamedatabase, passworddatabase FROM tb_tenant";

        // Criar um mapa onde a chave é o nome do banco e o valor é o DataSource correspondente
        Map<String, DataSource> tenantDataSources = new HashMap<>();

        jdbcTemplate.query(sql, (rs) -> {
            String databaseName = rs.getString("nomedatabase");

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/" + databaseName);
            dataSource.setUsername(rs.getString("usernamedatabase"));
            dataSource.setPassword(rs.getString("passworddatabase"));

            tenantDataSources.put(databaseName, dataSource);
        });

        return tenantDataSources;
    }

    public DataSource getDataSourceForMasterDatabase() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/master"); // Altere para o nome do seu banco de dados master
        dataSource.setUsername("postgres"); // Altere para o seu usuário do PostgreSQL
        dataSource.setPassword("admin"); // Altere para a senha do seu usuário do PostgreSQL
        return dataSource;
    }

    public DataSource getDataSourceForTenant(String tenantName) {
        Map<String, DataSource> tenantDataSources = getAllTenantDataSources();

        if (tenantDataSources.containsKey(tenantName)) {
            return tenantDataSources.get(tenantName);
        } else {
            throw new RuntimeException("❌ Erro: Banco de dados do tenant '" + tenantName + "' não encontrado.");
        }
    }
}