package com.emerald.infrastructure.transacao.entity;

import com.emerald.infrastructure.contabancaria.entity.ContaBancaria;
import com.emerald.infrastructure.categoria.entity.Categoria;
import com.emerald.infrastructure.metodopagamento.entity.MetodoPagamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tb_transacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String descricao; // Descrição textual da movimentação financeira

    @Column(nullable = false)
    private Double valor; // Valor monetário da transação (positivo para receitas, negativo para despesas)

    @Column(nullable = false, name = "data_transacao")
    @Temporal(TemporalType.DATE)
    private Date data; // Data de realização da transação

    @ManyToOne
    @JoinColumn(name = "conta_bancaria_id", nullable = false)
    private ContaBancaria contaBancaria; // Conta bancária de origem ou destino do saldo

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria; // Categoria para classificação e agrupamento da transação

    @ManyToOne
    @JoinColumn(name = "metodo_pagamento_id", nullable = false)
    private MetodoPagamento metodoPagamento; // Meio físico ou digital utilizado para o pagamento
}