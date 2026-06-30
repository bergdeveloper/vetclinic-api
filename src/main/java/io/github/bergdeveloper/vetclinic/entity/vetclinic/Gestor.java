package io.github.bergdeveloper.vetclinic.entity.vetclinic;


import io.github.bergdeveloper.vetclinic.entity.Endereco;
import io.github.bergdeveloper.vetclinic.entity.Usuario;
import io.github.bergdeveloper.vetclinic.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "tb_gestor")
@Getter
@Setter
@NoArgsConstructor
public class Gestor extends Usuario {
    private Date datacadastro;
    private String ip;

    public Gestor(Integer id, String cpf, String nome, String email, String senha, String telefone, Endereco endereco, Date datanascimento, Date datacadastro, Role role, String ip) {
        super(id, cpf, nome, email, senha, telefone, endereco, datanascimento, role);
        this.datacadastro = datacadastro;
        this.ip = ip;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_GESTOR_VETCLINIC"));

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
