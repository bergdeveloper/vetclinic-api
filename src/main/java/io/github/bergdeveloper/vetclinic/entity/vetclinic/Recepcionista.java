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
@Table(name = "tb_recepcionista")
@Getter
@Setter
@NoArgsConstructor
public class Recepcionista extends Usuario {
    private Date data_cadastro;
    private double salario;

    public Recepcionista(Integer id, String cpf, String nome, String email, String senha, String telefone, Endereco endereco, Date data_nascimento, Role role, Date data_cadastro, double salario) {
        super(id, cpf, nome, email, senha, telefone, endereco, data_nascimento, role);
        this.data_cadastro = data_cadastro;
        this.salario = salario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_RECEPCIONISTA_VETCLINIC"));

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
