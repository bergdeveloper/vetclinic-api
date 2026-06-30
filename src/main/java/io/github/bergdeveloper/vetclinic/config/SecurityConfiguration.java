package io.github.bergdeveloper.vetclinic.config;

import io.github.bergdeveloper.vetclinic.filter.FiltroDeSeguranca;
import io.github.bergdeveloper.vetclinic.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private FiltroDeSeguranca filtro;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Lê a variável de ambiente com a origem
        String allowedOrigin = System.getenv("ALLOWED_ORIGIN");

        // Se a variável de ambiente não estiver configurada, usa o valor default
        if (allowedOrigin == null || allowedOrigin.isEmpty()) {
            allowedOrigin = "http://127.0.0.1:5500"; // Valor default para desenvolvimento
        }

        // Declare 'allowedOrigin' como final dentro do método
        final String finalAllowedOrigin = allowedOrigin;

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(finalAllowedOrigin));  // Origem dinâmica e final
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/usuario/logar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tenants/create").hasAuthority("ROLE_ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.POST, "/api/gestores/cadastrar/recepcionista").hasAuthority("ROLE_GESTOR_VETCLINIC")
                        .requestMatchers(HttpMethod.GET, "/api/gestores/vergestores").hasAuthority("ROLE_GESTOR_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/recepcionista/cadastrar/cliente").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.GET, "/api/recepcionista/verclientes").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/usuario/cadastrar/cliente/animal").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.GET, "/api/raca/especie/").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/especie/cadastrar/especie").hasAuthority("ROLE_GESTOR_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/raca/cadastrar/raca").hasAuthority("ROLE_GESTOR_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/gestores/cadastrar/veterinario").hasAuthority("ROLE_GESTOR_VETCLINIC")
                        .requestMatchers(HttpMethod.GET, "/api/raca").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.GET, "/api/especie").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/agenda/cadastrarAgendamento").hasAnyAuthority("ROLE_RECEPCIONISTA_VETCLINIC", "ROLE_GESTOR_VETCLINIC", "ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.GET, "/api/agenda/meusAgendamentos").hasAuthority("ROLE_VETERINARIO_VETCLINIC")
                        .requestMatchers(HttpMethod.POST, "/api/usuario/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filtro, UsernamePasswordAuthenticationFilter.class)  // Adiciona o filtro de segurança antes do UsernamePasswordAuthenticationFilter
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Configura o encoder para a senha
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();  // Configura o AuthenticationManager
    }
}