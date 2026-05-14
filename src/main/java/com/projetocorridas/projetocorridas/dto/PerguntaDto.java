package com.projetocorridas.projetocorridas.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerguntaDto {
    private UUID id;
    private UUID corridaId;
    private String enunciado;
    private long respostaCorreta;
    private List<AlternativaDto> alternativas;

}
