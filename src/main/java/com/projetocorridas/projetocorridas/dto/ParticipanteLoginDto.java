package com.projetocorridas.projetocorridas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteLoginDto {
    private String nome;
    private String senha;
}