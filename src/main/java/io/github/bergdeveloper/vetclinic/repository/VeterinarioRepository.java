package io.github.bergdeveloper.vetclinic.repository;

import io.github.bergdeveloper.vetclinic.entity.vetclinic.Veterinario;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends UsuarioRepository<Veterinario> {

}