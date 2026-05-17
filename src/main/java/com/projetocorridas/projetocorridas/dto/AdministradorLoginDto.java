package com.projetocorridas.projetocorridas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorLoginDto {
    private String email;
    private String senha;
}