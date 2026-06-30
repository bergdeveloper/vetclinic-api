package io.github.bergdeveloper.vetclinic.repository;

import io.github.bergdeveloper.vetclinic.entity.vetclinic.Cliente;

import io.github.bergdeveloper.vetclinic.entity.vetclinic.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends UsuarioRepository<Cliente> {
}