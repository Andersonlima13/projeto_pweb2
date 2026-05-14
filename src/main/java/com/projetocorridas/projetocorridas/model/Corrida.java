package com.projetocorridas.projetocorridas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "corridas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Corrida {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    private Integer tempo;

    @OneToMany(mappedBy = "corridaId")
    private List<Pergunta> perguntas;

    private String titulo;
    private String descricao;
    private boolean isativa;

}
