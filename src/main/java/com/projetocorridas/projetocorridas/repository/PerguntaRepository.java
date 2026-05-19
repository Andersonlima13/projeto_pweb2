package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, UUID> {
    List<Pergunta> findByCorridaId(UUID corridaId);

    Optional<Pergunta> findByCorridaIdAndEnunciado(UUID corridaId, String enunciado);

}
