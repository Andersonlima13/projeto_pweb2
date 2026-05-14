package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projetocorridas.projetocorridas.dto.AlternativaDto;
import com.projetocorridas.projetocorridas.model.Alternativa;
import com.projetocorridas.projetocorridas.model.Pergunta;
import com.projetocorridas.projetocorridas.repository.AlternativaRepository;
import com.projetocorridas.projetocorridas.repository.PerguntaRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlternativaService {

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    private AlternativaDto entityToDto(Alternativa entity) {
        return AlternativaDto.builder()
                .id(entity.getId())
                .perguntaId(entity.getPergunta().getId())
                .descricao(entity.getDescricao())
                .isCorreta(entity.getIsCorreta())
                .build();
    }

    public AlternativaDto criar(AlternativaDto alternativaDto) {
        UUID perguntaId = alternativaDto.getPerguntaId();

        Pergunta pergunta = perguntaRepository.findById(perguntaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pergunta com ID " + perguntaId + " não encontrada"));

        validarAlternativaDto(alternativaDto);
        Alternativa alternativa = dtoToEntity(alternativaDto, pergunta);

        Alternativa salva = alternativaRepository.save(alternativa);
        return entityToDto(salva);
    }

    public AlternativaDto obter(UUID perguntaId, UUID alternativaId) {
        Alternativa alternativa = alternativaRepository.findById(alternativaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alternativa com ID " + alternativaId + " não encontrada"));

        if (!alternativa.getPergunta().getId().equals(perguntaId)) {
            throw new IllegalArgumentException(
                    "Alternativa com ID " + alternativaId + " não pertence à pergunta " + perguntaId);
        }

        return entityToDto(alternativa);
    }

    public List<AlternativaDto> listarPorPergunta(UUID perguntaId) {
        perguntaRepository.findById(perguntaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pergunta com ID " + perguntaId + " não encontrada"));

        return alternativaRepository.findByPerguntaId(perguntaId).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public AlternativaDto alterar(UUID perguntaId, AlternativaDto alternativaDto) {
        Alternativa alternativa = alternativaRepository.findById(alternativaDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alternativa com ID " + alternativaDto.getId() + " não encontrada"));

        if (!alternativa.getPergunta().getId().equals(perguntaId)) {
            throw new IllegalArgumentException(
                    "Alternativa com ID " + alternativaDto.getId() + " não pertence à pergunta " + perguntaId);
        }
        validarAlternativaDto(alternativaDto);
        alternativa.setDescricao(alternativaDto.getDescricao().trim());
        alternativa.setIsCorreta(Boolean.TRUE.equals(alternativaDto.getIsCorreta()));
        Alternativa atualizada = alternativaRepository.save(alternativa);
        return entityToDto(atualizada);
    }

    public void apagar(UUID perguntaId, UUID alternativaId) {
        Alternativa alternativa = alternativaRepository.findById(alternativaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alternativa com ID " + alternativaId + " não encontrada"));

        if (!alternativa.getPergunta().getId().equals(perguntaId)) {
            throw new IllegalArgumentException(
                    "Alternativa com ID " + alternativaId + " não pertence à pergunta " + perguntaId);
        }

        alternativaRepository.deleteById(alternativaId);
    }

    @Transactional
    public void substituirPorPergunta(UUID perguntaId, List<AlternativaDto> alternativas) {
        Pergunta pergunta = perguntaRepository.findById(perguntaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pergunta com ID " + perguntaId + " não encontrada"));

        List<Alternativa> alternativasExistentes = alternativaRepository.findByPerguntaId(perguntaId);
        alternativaRepository.deleteAll(alternativasExistentes);

        if (alternativas == null || alternativas.isEmpty()) {
            return;
        }

        List<Alternativa> novasAlternativas = alternativas.stream()
                .map(alternativaDto -> {
                    validarAlternativaDto(alternativaDto);
                    return dtoToEntity(alternativaDto, pergunta);
                })
                .collect(Collectors.toList());

        alternativaRepository.saveAll(novasAlternativas);
    }

    private Alternativa dtoToEntity(AlternativaDto alternativaDto, Pergunta pergunta) {
        Alternativa alternativa = new Alternativa();
        alternativa.setId(UUID.randomUUID());
        alternativa.setPergunta(pergunta);
        alternativa.setDescricao(alternativaDto.getDescricao().trim());
        alternativa.setIsCorreta(Boolean.TRUE.equals(alternativaDto.getIsCorreta()));
        return alternativa;
    }

    private void validarAlternativaDto(AlternativaDto alternativaDto) {
        if (alternativaDto.getDescricao() == null || alternativaDto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
    }

}
