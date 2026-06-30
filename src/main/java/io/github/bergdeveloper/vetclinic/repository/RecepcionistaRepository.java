package io.github.bergdeveloper.vetclinic.repository;


import io.github.bergdeveloper.vetclinic.entity.vetclinic.Gestor;
import io.github.bergdeveloper.vetclinic.entity.vetclinic.Recepcionista;
import org.springframework.stereotype.Repository;

@Repository
public interface RecepcionistaRepository extends UsuarioRepository<Recepcionista> {
}
