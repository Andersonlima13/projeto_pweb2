package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
}
