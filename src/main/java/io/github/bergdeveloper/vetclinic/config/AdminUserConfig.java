package io.github.bergdeveloper.vetclinic.config;



import io.github.bergdeveloper.vetclinic.entity.Endereco;
import io.github.bergdeveloper.vetclinic.entity.sistema.Administrador;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.repository.AdministradorRepository;
import io.github.bergdeveloper.vetclinic.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Administrador administradorSistema = administradorRepository.findByCpf("11111111111");
        if(administradorSistema != null){
            System.out.println("O Usuário administrador já está cadastrado.");
        }else{
            administradorSistema = new Administrador();
            administradorSistema.setCpf("11111111111");
            administradorSistema.setNome("BergDeveloper");
            administradorSistema.setEmail("BergDeveloper@gmail.com");
            administradorSistema.setSenha(passwordEncoder.encode("admin12345"));
            administradorSistema.setTelefone("+5587999999999");


            Endereco endereco = new Endereco();
            endereco.setRua("Rua aleatoria");
            endereco.setNumero(150);
            endereco.setBairro("centro");
            endereco.setCidade("cidade aleatoria");
            endereco.setEstado("estado do brasil");
            endereco.setCep(55555555);
            endereco.setComplemento("Casa");

            endereco = enderecoRepository.save(endereco);

            administradorSistema.setEndereco(endereco);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse("25/08/1999");
            administradorSistema.setData_nascimento(date);
            administradorSistema.setRole(Role.ADMINISTRADOR_SISTEMA);
            administradorRepository.save(administradorSistema);
        }
    }
}