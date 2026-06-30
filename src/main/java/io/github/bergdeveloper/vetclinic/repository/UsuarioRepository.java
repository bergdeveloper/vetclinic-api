package io.github.bergdeveloper.vetclinic.repository;


import io.github.bergdeveloper.vetclinic.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean // Evita que o Spring tente criar uma instância desse repositório
public interface UsuarioRepository<T extends Usuario> extends JpaRepository<T, Integer> {
    T findByCpf(String cpf);
}
