package com.projetocorridas.projetocorridas.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlternativaDto {
    private UUID id;
    private long corridaId;
    private long perguntaId;
    private String descricao;
    private Boolean isCorreta;

}
