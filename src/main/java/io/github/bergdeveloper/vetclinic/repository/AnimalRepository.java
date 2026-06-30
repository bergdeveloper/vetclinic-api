package io.github.bergdeveloper.vetclinic.repository;

import io.github.bergdeveloper.vetclinic.entity.vetclinic.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
