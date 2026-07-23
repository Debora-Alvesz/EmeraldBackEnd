package com.emerald.infrastructure.contabancaria.entity.enums;

public enum TipoConta {

    // Tipos de conta disponíveis no sistema
    CORRENTE("Conta Corrente"),
    POUPANCA("Poupança"),
    INVESTIMENTO("Investimentos"),
    DINHEIRO("Dinheiro");

    // Atributo para guardar o nome formatado
    private final String descricao;

    // Construtor
    TipoConta(String descricao) {
        this.descricao = descricao;
    }

    // Método para pegar o nome formatado
    public String getDescricao() {
        return descricao;
    }
}