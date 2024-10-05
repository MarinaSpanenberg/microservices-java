package br.edu.atitus.paradigma.produtoservice.repositories;

import br.edu.atitus.paradigma.produtoservice.entities.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository <ProdutoEntity, Integer> {
}
