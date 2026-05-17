package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Participante;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, UUID> {
    Optional<Participante> findByNomeAndSenha(String nome, String senha);

    List<Participante> findByCorrida_Id(UUID corridaId);
}
