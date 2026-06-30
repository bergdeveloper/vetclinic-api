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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Animal> animais;

    public Cliente(Integer id, String cpf, String nome, String email, String senha, String telefone, Endereco endereco, Date data_nascimento, Role role, List<Animal> animais) {
        super(id, cpf, nome, email, senha, telefone, endereco, data_nascimento, role);
        this.animais = animais;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE_VETCLINIC"));

        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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