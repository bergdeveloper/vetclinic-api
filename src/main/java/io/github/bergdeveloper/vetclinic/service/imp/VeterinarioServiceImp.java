package io.github.bergdeveloper.vetclinic.service.imp;

import io.github.bergdeveloper.vetclinic.dto.VeterinarioDTO;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Veterinario;
import io.github.bergdeveloper.vetclinic.enums.Role;
import io.github.bergdeveloper.vetclinic.repository.ClienteRepository;
import io.github.bergdeveloper.vetclinic.repository.VeterinarioRepository;
import io.github.bergdeveloper.vetclinic.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImp implements VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> salvar(VeterinarioDTO veterinarioDTO){
        if(clienteRepository.findByCpf(veterinarioDTO.cpf()) != null || veterinarioRepository.findByCpf(veterinarioDTO.cpf()) != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF do usuário já cadastrado em nosso banco de dados.");
        }

        Veterinario veterinario = new Veterinario();
        veterinario.setCpf(veterinarioDTO.cpf());
        veterinario.setNome(veterinarioDTO.nome());
        veterinario.setEmail(veterinarioDTO.email());
        veterinario.setSenha(passwordEncoder.encode(veterinarioDTO.senha()));
        veterinario.setTelefone(veterinarioDTO.telefone());
        veterinario.setEndereco(veterinarioDTO.endereco());
        veterinario.setData_nascimento(veterinarioDTO.datanascimento());
        veterinario.setRole(Role.VETERINARIO);
        veterinario.setCrmv(veterinarioDTO.crmv());
        veterinario.setEspecialidade(veterinarioDTO.especialidade());

        veterinarioRepository.save(veterinario);
        return new ResponseEntity<>("Conta criada com sucesso.", HttpStatus.CREATED);
    }
}
