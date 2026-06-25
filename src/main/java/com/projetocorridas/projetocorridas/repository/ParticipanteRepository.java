package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Participante;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, UUID> {

    Optional<Participante> findByNomeAndSenha(String nome, String senha);

    Page<Participante> findAllByOrderByPontosDesc(Pageable pageable);

    Optional<Participante> findByNome(String nome);

    List<Participante> findByCorridas_Id(UUID corridaId);
}