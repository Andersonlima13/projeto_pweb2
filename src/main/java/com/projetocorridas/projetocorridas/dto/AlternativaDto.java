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
    private UUID perguntaId;
    private String descricao;
    private Boolean correta;

}
