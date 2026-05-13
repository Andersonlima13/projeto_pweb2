package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Corrida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorridaRepository extends JpaRepository<Corrida, Long> {
}
