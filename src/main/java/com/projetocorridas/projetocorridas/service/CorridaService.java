package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.model.Corrida;
import com.projetocorridas.projetocorridas.repository.CorridaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CorridaService {

    @Autowired
    private CorridaRepository corridaRepository;

    @Autowired
    private PerguntaService perguntaService;

    private Corrida dtoToEntity(CorridaDto dto) {
        Corrida corrida = new Corrida();
        corrida.setId(dto.getId());
        corrida.setTitulo(dto.getTitulo());
        corrida.setDescricao(dto.getDescricao());
        return corrida;
    }

    private CorridaDto entityToDto(Corrida entity) {
        return CorridaDto.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .perguntas(new ArrayList<>())
                .build();
    }

    public CorridaDto criar(CorridaDto corridaDto) {
        validarCorridaDto(corridaDto);
        corridaDto.setId(null);
        if (corridaDto.getPerguntas() == null) {
            corridaDto.setPerguntas(new ArrayList<>());
        }
        Corrida corrida = dtoToEntity(corridaDto);
        Corrida salva = corridaRepository.save(corrida);
        return entityToDto(salva);
    }

    public CorridaDto obter(UUID id) {
        Corrida corrida = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));
        return entityToDto(corrida);
    }

    public List<CorridaDto> listarTodas() {
        return corridaRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public CorridaDto alterar(CorridaDto corridaDto) {
        UUID id = corridaDto.getId();
        Corrida corridaExistente = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        if (!corridaExistente.getTitulo().equals(corridaDto.getTitulo())) {
            verificaNomeDuplicado(corridaDto.getTitulo());
        }

        corridaExistente.setTitulo(corridaDto.getTitulo());
        corridaExistente.setDescricao(corridaDto.getDescricao());

        Corrida atualizada = corridaRepository.save(corridaExistente);
        return entityToDto(atualizada);
    }

    @Transactional
    public void apagar(UUID id) {
        corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        List<PerguntaDto> perguntas = perguntaService.listarPorCorrida(id);
        for (PerguntaDto pergunta : perguntas) {
            perguntaService.apagar(id, pergunta.getId());
        }

        corridaRepository.deleteById(id);
    }

    private void validarCorridaDto(CorridaDto corridaDto) {
        verificaNomeDuplicado(corridaDto.getTitulo());
        if (corridaDto.getTitulo() == null || corridaDto.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (corridaDto.getDescricao() == null || corridaDto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
    }

    public void verificaNomeDuplicado(String titulo) {
        corridaRepository.findByTitulo(titulo).ifPresent(c -> {
            throw new IllegalArgumentException("Título de corrida já existe: " + titulo);
        });
    }

    public void adicionarPergunta(UUID corridaId, PerguntaDto perguntaDto) {
        corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));
    }

    public void removerPergunta(UUID corridaId, UUID perguntaId) {
        corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));

    }

}
