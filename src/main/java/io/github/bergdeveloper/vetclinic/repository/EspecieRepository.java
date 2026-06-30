package io.github.bergdeveloper.vetclinic.repository;

import io.github.bergdeveloper.vetclinic.entity.vetclinic.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Long> {
}
