package com.projetocorridas.projetocorridas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.model.EstadoCorrida;
import com.projetocorridas.projetocorridas.model.Corrida;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.CorridaRepository;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CorridaService {

    @Autowired
    private CorridaRepository corridaRepository;

    @Autowired
    private PerguntaService perguntaService;

    @Autowired
    private ParticipanteRepository participanteRepository;

    private Corrida dtoToEntity(CorridaDto dto) {
        Corrida corrida = new Corrida();
        corrida.setId(dto.getId());
        corrida.setTitulo(dto.getTitulo());
        corrida.setDescricao(dto.getDescricao());
        corrida.setEstadoCorrida(dto.getEstadoCorrida() == null ? EstadoCorrida.EM_ANDAMENTO : dto.getEstadoCorrida());
        return corrida;
    }

    private CorridaDto entityToDto(Corrida entity) {
        Set<UUID> participantesIds = entity.getParticipantes() != null
                ? entity.getParticipantes().stream()
                        .map(Participante::getId)
                        .collect(Collectors.toSet())
                : new HashSet<>();

        return CorridaDto.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .estadoCorrida(
                        entity.getEstadoCorrida() == null ? EstadoCorrida.EM_ANDAMENTO : entity.getEstadoCorrida())
                .perguntas(new ArrayList<>())
                .participantesIds(participantesIds)
                .temFoto(entity.getFoto() != null && entity.getFoto().length > 0)
                .build();
    }

    public CorridaDto criar(CorridaDto corridaDto) {
        validarCorridaDto(corridaDto);
        corridaDto.setId(null);
        if (corridaDto.getPerguntas() == null) {
            corridaDto.setPerguntas(new ArrayList<>());
        }
        if (corridaDto.getEstadoCorrida() == null) {
            corridaDto.setEstadoCorrida(EstadoCorrida.EM_ANDAMENTO);
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
    public CorridaDto finalizar(UUID id) {
        Corrida corrida = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        corrida.setEstadoCorrida(EstadoCorrida.REALIZADA);
        Corrida atualizada = corridaRepository.save(corrida);
        return entityToDto(atualizada);
    }

    @Transactional
    public void apagar(UUID id) {
        corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        List<Participante> participantes = participanteRepository.findByCorridas_Id(id);
        for (Participante participante : participantes) {
            participante.getCorridas().removeIf(corrida -> corrida.getId().equals(id));
        }
        participanteRepository.saveAll(participantes);

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

    @Transactional
    public void adicionarParticipante(UUID corridaId, UUID participanteId) {
        Corrida corrida = corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Participante com ID " + participanteId + " não encontrado"));

        // Verificar se já está associado
        if (corrida.getParticipantes().stream().anyMatch(p -> p.getId().equals(participanteId))) {
            throw new IllegalArgumentException("Participante já está associado a esta corrida");
        }

        corrida.getParticipantes().add(participante);
        participante.getCorridas().add(corrida);

        corridaRepository.save(corrida);
        participanteRepository.save(participante);
    }

    @Transactional
    public void removerParticipante(UUID corridaId, UUID participanteId) {
        Corrida corrida = corridaRepository.findById(corridaId)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + corridaId + " não encontrada"));

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Participante com ID " + participanteId + " não encontrado"));

        corrida.getParticipantes().removeIf(p -> p.getId().equals(participanteId));
        participante.getCorridas().removeIf(c -> c.getId().equals(corridaId));

        corridaRepository.save(corrida);
        participanteRepository.save(participante);
    }

    public CorridaDto salvarFoto(UUID id, org.springframework.web.multipart.MultipartFile arquivo) {
        Corrida corrida = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        try {
            corrida.setFoto(arquivo.getBytes());
            corrida.setFotoTipo(arquivo.getContentType());
            Corrida atualizada = corridaRepository.save(corrida);
            return entityToDto(atualizada);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException(
                    "Não foi possível carregar a imagem: " + arquivo.getOriginalFilename() + ". Erro: "
                            + e.getMessage());
        }
    }

    public Corrida obterFotoEntity(UUID id) {
        return corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));
    }

    @Transactional
    public CorridaDto removerFoto(UUID id) {
        Corrida corrida = corridaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corrida com ID " + id + " não encontrada"));

        corrida.setFoto(null);
        corrida.setFotoTipo(null);

        Corrida atualizada = corridaRepository.save(corrida);
        return entityToDto(atualizada);
    }

}
