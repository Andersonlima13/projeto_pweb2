package com.projetocorridas.projetocorridas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pergunta {
    private long id;
    private long corridaId;
    private String enunciado;
    private Integer respostaCorreta;
    private List<Alternativa> alternativas;
}
