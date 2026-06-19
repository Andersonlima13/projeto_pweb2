package com.projetocorridas.projetocorridas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ParticipanteDto {
    private UUID id;
    private String nome;
    private String senha;
    private Boolean admin;
    private Integer pontos;
    @Builder.Default
    private List<UUID> corridaIds = new ArrayList<>();
    @Builder.Default
    private List<String> corridaTitulos = new ArrayList<>();

}
