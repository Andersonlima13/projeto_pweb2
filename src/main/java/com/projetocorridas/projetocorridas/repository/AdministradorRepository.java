package com.projetocorridas.projetocorridas.repository;

import com.projetocorridas.projetocorridas.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByEmailAndSenha(String email, String senha);

    Optional<Administrador> findByEmail(String email);
}
