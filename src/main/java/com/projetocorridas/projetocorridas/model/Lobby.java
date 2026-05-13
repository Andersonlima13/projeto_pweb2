package com.projetocorridas.projetocorridas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lobby {
    private List<Participante> participantes; // em teoria , todos os participantes comecam no lobby
    private List<Corrida> corridas;

}
