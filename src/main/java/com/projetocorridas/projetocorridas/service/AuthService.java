package com.projetocorridas.projetocorridas.service;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    public Optional<Participante> autenticar(ParticipanteDto dto) {
        return participanteRepository.findByNomeAndSenha(dto.getNome(), dto.getSenha());
    }
}
