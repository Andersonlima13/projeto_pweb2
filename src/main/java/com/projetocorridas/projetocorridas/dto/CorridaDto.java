package com.projetocorridas.projetocorridas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

import com.projetocorridas.projetocorridas.model.EstadoCorrida;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorridaDto {

    private UUID id;
    private String titulo;
    private String descricao;
    private EstadoCorrida estadoCorrida;
    private java.util.List<PerguntaDto> perguntas;

}
