package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlternativaRepository extends JpaRepository<Alternativa, UUID> {
}
