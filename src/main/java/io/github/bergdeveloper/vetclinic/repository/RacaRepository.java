package io.github.bergdeveloper.vetclinic.repository;

import io.github.bergdeveloper.vetclinic.entity.vetclinic.Raca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RacaRepository extends JpaRepository<Raca, Long> {
}
