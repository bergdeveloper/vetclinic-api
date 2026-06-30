package io.github.bergdeveloper.vetclinic.entity.sistema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "tb_tenant")
@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpf;
    private String nomedatabase;
    private String usernamedatabase;
    private String passworddatabase;
    private String email;
}