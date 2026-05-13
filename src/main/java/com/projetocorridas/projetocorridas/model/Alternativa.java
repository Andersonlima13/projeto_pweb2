package com.projetocorridas.projetocorridas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alternativa {
    private long id;
    private String descricao;
    private Boolean isCorreta;

}
