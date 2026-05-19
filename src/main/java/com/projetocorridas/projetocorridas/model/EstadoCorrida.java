package com.projetocorridas.projetocorridas.model;

public enum EstadoCorrida {
    EM_ANDAMENTO("em andamento"),
    REALIZADA("realizada");

    private final String descricao;

    EstadoCorrida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}