package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Converter DTO para Entity
    private Corrida dtoToEntity(CorridaDto dto) {
        Corrida corrida = new Corrida();
        corrida.setId(dto.getId());
        corrida.setTitulo(dto.getTitulo());
        corrida.setDescricao(dto.getDescricao());
        corrida.setTempo(dto.getTempo());
        return corrida;
    }

    // Converter Entity para DTO
    private CorridaDto entityToDto(Corrida entity) {
        return CorridaDto.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .tempo(entity.getTempo())
                .perguntas(new ArrayList<>())
                .build();
    }

    // Criar nova corrida
    public CorridaDto criar(CorridaDto corridaDto) {
        validarCorridaDto(corridaDto);
        corridaDto.setId(UUID.randomUUID());
        if (corridaDto.getPerguntas() == null) {
            corridaDto.setPerguntas(new ArrayList<>());
        }
        Corrida corrida = dtoToEntity(corridaDto);
        Corrida salva = corridaRepository.save(corrida);
        return entityToDto(salva);
    }

    // Obter corrida por ID
    public CorridaDto obter(UUID id) {
        Corrida corrida = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));
        return entityToDto(corrida);
    }

    // Listar todas as corridas
    public List<CorridaDto> listarTodas() {
        return corridaRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // Alterar corrida existente
    public CorridaDto alterar(CorridaDto corridaDto) {
        UUID id = corridaDto.getId();
        Corrida corridaExistente = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        // Verifica duplicação de título apenas se o título foi alterado
        if (!corridaExistente.getTitulo().equals(corridaDto.getTitulo())) {
            verificaNomeDuplicado(corridaDto.getTitulo());
        }

        corridaExistente.setTitulo(corridaDto.getTitulo());
        corridaExistente.setDescricao(corridaDto.getDescricao());
        corridaExistente.setTempo(corridaDto.getTempo());

        Corrida atualizada = corridaRepository.save(corridaExistente);
        return entityToDto(atualizada);
    }

    // Apagar corrida
    public void apagar(UUID id) {
        corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));
        corridaRepository.deleteById(id);
    }

    // Validar corrida DTO
    private void validarCorridaDto(CorridaDto corridaDto) {
        verificaNomeDuplicado(corridaDto.getTitulo());
        if (corridaDto.getTempo() == null) {
            throw new IllegalArgumentException("Tempo é obrigatório");
        }
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

    // Adicionar pergunta à corrida
    public void adicionarPergunta(UUID corridaId, PerguntaDto perguntaDto) {
        // Valida se a corrida existe
        corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));
        // Método existe por compatibilidade, mas a lógica de adicionar pergunta é
        // handled pelo PerguntaService
    }

    // Remover pergunta da corrida
    public void removerPergunta(UUID corridaId, UUID perguntaId) {
        // Valida se a corrida existe
        corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));
        // Método existe por compatibilidade, mas a lógica de remover pergunta é handled
        // pelo PerguntaService
    }

}
