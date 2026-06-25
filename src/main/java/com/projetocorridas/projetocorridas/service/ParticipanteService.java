package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.model.Corrida;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.CorridaRepository;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private CorridaRepository corridaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ParticipanteDto criar(ParticipanteDto participanteDto) {
        validarParticipanteDto(participanteDto);
        Participante participante = new Participante();
        participante.setNome(participanteDto.getNome());
        participante.setSenha(passwordEncoder.encode(participanteDto.getSenha()));
        participante.setAdmin(false);
        participante.setPontos(participanteDto.getPontos() == null ? 0 : participanteDto.getPontos());
        participante.setCorridas(obterCorridas(participanteDto.getCorridaIds()));

        Participante salvo = participanteRepository.save(participante);
        return mapToDto(salvo);
    }

    public ParticipanteDto obter(UUID id) {
        Participante participante = participanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado"));
        return mapToDto(participante);
    }

    public List<ParticipanteDto> listarTodos() {
        return participanteRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Page<ParticipanteDto> listarRanking(Pageable pageable) {

        return participanteRepository
                .findAllByOrderByPontosDesc(pageable)
                .map(this::mapToDto);
    }

    public ParticipanteDto alterar(ParticipanteDto participanteDto) {
        UUID id = participanteDto.getId();
        Participante existente = participanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado"));

        existente.setNome(participanteDto.getNome());
        if (participanteDto.getSenha() != null && !participanteDto.getSenha().isBlank()) {
            existente.setSenha(passwordEncoder.encode(participanteDto.getSenha()));
        }
        existente.setAdmin(false);
        existente.setPontos(participanteDto.getPontos() == null ? existente.getPontos() : participanteDto.getPontos());
        existente.setCorridas(obterCorridas(participanteDto.getCorridaIds()));

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

    public void atualizarParticipantesDaCorrida(UUID corridaId, List<UUID> participanteIds) {
        Corrida corrida = corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));
        Set<UUID> idsAssociados = participanteIds == null ? new HashSet<>() : new HashSet<>(participanteIds);

        List<Participante> participantes = participanteRepository.findAll();
        for (Participante participante : participantes) {
            boolean deveEstarAssociado = idsAssociados.contains(participante.getId());
            boolean jaEstaAssociado = participante.getCorridas().stream()
                    .anyMatch(corridaAtual -> corridaAtual.getId().equals(corridaId));

            if (deveEstarAssociado && !jaEstaAssociado) {
                participante.getCorridas().add(corrida);
            }

            if (!deveEstarAssociado && jaEstaAssociado) {
                participante.getCorridas().removeIf(corridaAtual -> corridaAtual.getId().equals(corridaId));
            }
        }

        participanteRepository.saveAll(participantes);
    }

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
                .corridaIds(participante.getCorridas().stream()
                        .map(Corrida::getId)
                        .collect(Collectors.toList()))
                .corridaTitulos(participante.getCorridas().stream()
                        .map(Corrida::getTitulo)
                        .collect(Collectors.toList()))
                .build();
    }

    private List<Corrida> obterCorridas(List<UUID> corridaIds) {
        if (corridaIds == null || corridaIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Corrida> corridas = new ArrayList<>();
        for (UUID corridaId : corridaIds) {
            if (corridaId == null) {
                continue;
            }
            Corrida corrida = corridaRepository.findById(corridaId)
                    .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));
            corridas.add(corrida);
        }
        return corridas;
    }

    private void validarParticipanteDto(ParticipanteDto participanteDto) {
        if (participanteDto.getNome() == null || participanteDto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (participanteDto.getSenha() == null || participanteDto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
    }

}
