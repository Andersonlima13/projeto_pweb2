package com.projetocorridas.projetocorridas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "alternativas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alternativa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pergunta_id", nullable = false, foreignKey = @ForeignKey(name = "fk_alternativa_pergunta"))
    private Pergunta pergunta;

    private String descricao;
    private Boolean correta;

}
