package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    // Criar novo participante
    public ParticipanteDto criar(ParticipanteDto participanteDto) {
        validarParticipanteDto(participanteDto);
        Participante participante = new Participante();
        participante.setNome(participanteDto.getNome());
        participante.setSenha(participanteDto.getSenha());

        Participante salvo = participanteRepository.save(participante);
        return mapToDto(salvo);
    }

    // Obter participante por ID
    public ParticipanteDto obter(UUID id) {
        Participante participante = participanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado"));
        return mapToDto(participante);
    }

    // Listar todos os participantes
    public List<ParticipanteDto> listarTodos() {
        return participanteRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Alterar participante
    public ParticipanteDto alterar(ParticipanteDto participanteDto) {
        UUID id = participanteDto.getId();
        Participante existente = participanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado"));

        existente.setNome(participanteDto.getNome());
        existente.setSenha(participanteDto.getSenha());

        Participante salvo = participanteRepository.save(existente);
        return mapToDto(salvo);
    }

    // Apagar participante
    public void apagar(UUID id) {
        if (!participanteRepository.existsById(id)) {
            throw new IllegalArgumentException("Participante com ID " + id + " não encontrado");
        }
        participanteRepository.deleteById(id);
    }

    private ParticipanteDto mapToDto(Participante participante) {
        return ParticipanteDto.builder()
                .id(participante.getId())
                .nome(participante.getNome())
                .senha(participante.getSenha())
                .build();
    }

    // Validar participante DTO
    private void validarParticipanteDto(ParticipanteDto participanteDto) {
        if (participanteDto.getNome() == null || participanteDto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (participanteDto.getSenha() == null || participanteDto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
    }
}
