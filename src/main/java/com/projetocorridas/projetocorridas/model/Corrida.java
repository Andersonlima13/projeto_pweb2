package com.projetocorridas.projetocorridas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
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

    @Transient
    private List<Pergunta> perguntas;

    @ManyToMany(mappedBy = "corridas", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Participante> participantes = new ArrayList<>();

    private String titulo;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private EstadoCorrida estadoCorrida = EstadoCorrida.EM_ANDAMENTO;
    private boolean isativa;

}
