package com.emerald.infrastructure.transacao.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TransacaoResponseDTO {
    private UUID id;
    private String descricao;
    private Double valor;
    private Date data;

    private UUID contaBancariaId;
    private String nomeContaBancaria; // Campo para exibição direta no front-end

    private UUID categoriaId;
    private String nomeCategoria; // Campo para exibição direta no front-end

    private Long metodoPagamentoId;
    private String nomeMetodoPagamento; // Campo para exibição direta no front-end
}