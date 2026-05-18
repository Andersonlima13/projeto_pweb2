package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.model.Corrida;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.CorridaRepository;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private CorridaRepository corridaRepository;

    // Criar novo participante
    public ParticipanteDto criar(ParticipanteDto participanteDto) {
        validarParticipanteDto(participanteDto);
        Participante participante = new Participante();
        participante.setNome(participanteDto.getNome());
        participante.setSenha(participanteDto.getSenha());
        participante.setAdmin(false);
        participante.setPontos(participanteDto.getPontos() == null ? 0 : participanteDto.getPontos());
        participante.setCorrida(obterCorridaOpcional(participanteDto.getCorridaId()));

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
        existente.setAdmin(false);
        existente.setPontos(participanteDto.getPontos() == null ? existente.getPontos() : participanteDto.getPontos());
        existente.setCorrida(obterCorridaOpcional(participanteDto.getCorridaId()));

        Participante salvo = participanteRepository.save(existente);
        return mapToDto(salvo);
    }

    public ParticipanteDto incrementarPontos(UUID id, Integer pontos) {
        Participante participante = participanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado"));

        int pontosAtuais = participante.getPontos() == null ? 0 : participante.getPontos();
        int incremento = pontos == null ? 0 : pontos;
        participante.setPontos(pontosAtuais + incremento);

        Participante salvo = participanteRepository.save(participante);
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
                .admin(participante.isAdmin())
                .pontos(participante.getPontos())
                .corridaId(participante.getCorrida() == null ? null : participante.getCorrida().getId())
                .corridaTitulo(participante.getCorrida() == null ? null : participante.getCorrida().getTitulo())
                .build();
    }

    private Corrida obterCorridaOpcional(UUID corridaId) {
        if (corridaId == null) {
            return null;
        }

        return corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));
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
