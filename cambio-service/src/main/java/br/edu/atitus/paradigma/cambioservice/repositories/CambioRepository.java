package br.edu.atitus.paradigma.cambioservice.repositories;

import br.edu.atitus.paradigma.cambioservice.entities.CambioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CambioRepository extends JpaRepository<CambioEntity, String> {

    Optional<CambioEntity> findByOrigemAndDestino(String origem, String destino);
}
