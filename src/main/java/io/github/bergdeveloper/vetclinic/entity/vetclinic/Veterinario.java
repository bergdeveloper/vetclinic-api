package io.github.bergdeveloper.vetclinic.entity.vetclinic;



import io.github.bergdeveloper.vetclinic.entity.Endereco;
import io.github.bergdeveloper.vetclinic.entity.Usuario;
import io.github.bergdeveloper.vetclinic.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_veterinario")
@Getter
@Setter
@NoArgsConstructor
public class Veterinario extends Usuario {
    private Date datacadastro;
    private String crmv;
    private String especialidade;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Agenda> agendas;

    public Veterinario(Integer id, String cpf, String nome, String email, String senha, String telefone, Endereco endereco, Date datanascimento, Date datacadastro, Role role, String crmv, String especialidade) {
        super(id, cpf, nome, email, senha, telefone, endereco, datanascimento, role);
        this.datacadastro = datacadastro;
        this.crmv = crmv;
        this.especialidade = especialidade;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_VETERINARIO_VETCLINIC"));

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.cpf;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
