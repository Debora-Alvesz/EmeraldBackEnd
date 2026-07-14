package com.emerald.infrastructure.metodopagamento.dto;

import lombok.Data;

@Data
public class MetodoPagamentoResponseDTO {
    private Long id;
    private String nomeMetodo; // Nome mapeado que o React usará para renderizar as opções na tela
}