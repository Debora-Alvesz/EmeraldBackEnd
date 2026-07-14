package com.emerald.infrastructure.metodopagamento.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_metodo_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único auto-incrementado

    @Column(name = "nome_metodo", nullable = false, unique = true, length = 100)
    private String nomeMetodo; // Nome da forma de pagamento (ex: Pix, Crédito, Débito)
}