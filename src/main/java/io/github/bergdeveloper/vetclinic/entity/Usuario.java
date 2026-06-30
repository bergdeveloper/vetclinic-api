package io.github.bergdeveloper.vetclinic.entity;


import io.github.bergdeveloper.vetclinic.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(unique = true)
    protected String cpf;

    protected String nome;
    protected String email;
    protected String senha;
    protected String telefone;
    @OneToOne
    @JoinColumn(name = "endereco", referencedColumnName = "id")
    protected Endereco endereco;
    protected Date data_nascimento;
    protected Role role;

}
