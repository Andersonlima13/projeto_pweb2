package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.dto.AlternativaDto;
import com.projetocorridas.projetocorridas.model.Pergunta;
import com.projetocorridas.projetocorridas.repository.PerguntaRepository;
import com.projetocorridas.projetocorridas.repository.CorridaRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PerguntaService {

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private CorridaRepository corridaRepository;

    private Pergunta dtoToEntity(PerguntaDto dto) {
        Pergunta pergunta = new Pergunta();
        pergunta.setId(dto.getId());
        pergunta.setCorridaId(dto.getCorridaId());
        pergunta.setEnunciado(dto.getEnunciado());
        pergunta.setRespostaCorreta(Math.toIntExact(dto.getRespostaCorreta()));
        return pergunta;
    }

    private PerguntaDto entityToDto(Pergunta entity) {
        return PerguntaDto.builder()
                .id(entity.getId())
                .corridaId(entity.getCorridaId())
                .enunciado(entity.getEnunciado())
                .respostaCorreta(entity.getRespostaCorreta())
                .build();
    }

    public PerguntaDto criar(PerguntaDto perguntaDto) {
        UUID corridaId = perguntaDto.getCorridaId();

        corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));

        validarPerguntaDto(perguntaDto);

        perguntaDto.setId(UUID.randomUUID());
        Pergunta pergunta = dtoToEntity(perguntaDto);
        Pergunta salva = perguntaRepository.save(pergunta);

        return entityToDto(salva);
    }

    public PerguntaDto obter(UUID corridaId, UUID perguntaId) {
        Pergunta pergunta = perguntaRepository.findById(perguntaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pergunta com ID " + perguntaId + " não encontrada na corrida " + corridaId));

        if (!pergunta.getCorridaId().equals(corridaId)) {
            throw new IllegalArgumentException(
                    "Pergunta com ID " + perguntaId + " não encontrada na corrida " + corridaId);
        }

        return entityToDto(pergunta);
    }

    public List<PerguntaDto> listarPorCorrida(UUID corridaId) {
        corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));

        return perguntaRepository.findByCorridaId(corridaId).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public PerguntaDto alterar(PerguntaDto perguntaDto) {
        UUID perguntaId = perguntaDto.getId();
        UUID corridaId = perguntaDto.getCorridaId();

        Pergunta perguntaExistente = perguntaRepository.findById(perguntaId)
                .orElseThrow(() -> new IllegalArgumentException("Pergunta com ID " + perguntaId + " não encontrada"));

        if (!perguntaExistente.getCorridaId().equals(corridaId)) {
            throw new IllegalArgumentException(
                    "Pergunta com ID " + perguntaId + " não encontrada na corrida " + corridaId);
        }

        validarPerguntaDto(perguntaDto);

        perguntaExistente.setEnunciado(perguntaDto.getEnunciado());
        perguntaExistente.setRespostaCorreta(Math.toIntExact(perguntaDto.getRespostaCorreta()));

        Pergunta atualizada = perguntaRepository.save(perguntaExistente);
        return entityToDto(atualizada);
    }

    public void apagar(UUID corridaId, UUID perguntaId) {
        Pergunta pergunta = perguntaRepository.findById(perguntaId)
                .orElseThrow(() -> new IllegalArgumentException("Pergunta com ID " + perguntaId + " não encontrada"));

        if (!pergunta.getCorridaId().equals(corridaId)) {
            throw new IllegalArgumentException(
                    "Pergunta com ID " + perguntaId + " não encontrada na corrida " + corridaId);
        }

        perguntaRepository.deleteById(perguntaId);
    }

    private void validarPerguntaDto(PerguntaDto perguntaDto) {
        if (perguntaDto.getEnunciado() == null || perguntaDto.getEnunciado().trim().isEmpty()) {
            throw new IllegalArgumentException("Enunciado é obrigatório");
        }
        if (perguntaDto.getRespostaCorreta() <= 0) {
            throw new IllegalArgumentException("Resposta correta é obrigatória");
        }
    }

    public void adicionarAlternativa(UUID corridaId, UUID perguntaId, AlternativaDto alternativaDto) {
        PerguntaDto pergunta = obter(corridaId, perguntaId);
        if (pergunta != null) {
            if (pergunta.getAlternativas() == null) {
                pergunta.setAlternativas(new java.util.ArrayList<>());
            }
            pergunta.getAlternativas().add(alternativaDto);
        }
    }

    public void removerAlternativa(UUID corridaId, UUID perguntaId, AlternativaDto alternativaDto) {
        PerguntaDto pergunta = obter(corridaId, perguntaId);
        if (pergunta != null && pergunta.getAlternativas() != null) {
            pergunta.getAlternativas().removeIf(a -> a.getId().equals(alternativaDto.getId()));
        }
    }
}