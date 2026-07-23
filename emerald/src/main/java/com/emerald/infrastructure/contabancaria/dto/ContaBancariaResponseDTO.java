package com.emerald.infrastructure.contabancaria.dto;

import com.emerald.infrastructure.contabancaria.entity.enums.TipoConta;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de resposta que retorna os dados da conta bancária de forma segura para o front-end.
 */
@Data
public class ContaBancariaResponseDTO {
    private UUID id;

    private String nomeConta;

    private BigDecimal saldo;

    private TipoConta tipoConta;

    private UUID usuarioId; // Retorna apenas o ID do dono da conta
}