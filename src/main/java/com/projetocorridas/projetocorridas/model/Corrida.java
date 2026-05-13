package com.projetocorridas.projetocorridas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Corrida {
    private long id;
    private Integer tempo;
    private List<Pergunta> perguntas;
    private String titulo;
    private String descricao;
    private boolean isativa;

}
