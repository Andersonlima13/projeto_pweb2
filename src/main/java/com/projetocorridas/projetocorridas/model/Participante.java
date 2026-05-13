package com.projetocorridas.projetocorridas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participante {
    private long id;
    private String nome;
    private String senha;
}