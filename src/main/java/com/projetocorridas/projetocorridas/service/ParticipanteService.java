package com.projetocorridas.projetocorridas.service;

import org.springframework.stereotype.Service;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class ParticipanteService {
    private final Map<UUID, ParticipanteDto> participantesMap = new HashMap<>();

    // Criar novo participante
    public ParticipanteDto criar(ParticipanteDto participanteDto) {
        validarParticipanteDto(participanteDto);
        participanteDto.setId(UUID.randomUUID());
        participantesMap.put(participanteDto.getId(), participanteDto);
        return participanteDto;
    }

    // Obter participante por ID
    public ParticipanteDto obter(UUID id) {
        return Optional.ofNullable(participantesMap.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado"));
    }

    // Listar todos os participantes
    public List<ParticipanteDto> listarTodos() {
        return List.copyOf(participantesMap.values());
    }

    // Alterar participante
    public ParticipanteDto alterar(ParticipanteDto participanteDto) {
        UUID id = participanteDto.getId();
        validarParticipanteExiste(id);

        participantesMap.put(id, participanteDto);
        return participanteDto;
    }

    // Apagar participante
    public void apagar(UUID id) {
        validarParticipanteExiste(id);
        participantesMap.remove(id);
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

    // Validar se participante existe
    private void validarParticipanteExiste(UUID id) {
        if (!participantesMap.containsKey(id)) {
            throw new IllegalArgumentException("Participante com ID " + id + " não encontrado");
        }
    }
}
