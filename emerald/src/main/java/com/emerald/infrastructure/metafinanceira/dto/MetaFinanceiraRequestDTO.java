package com.emerald.infrastructure.metafinanceira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class MetaFinanceiraRequestDTO {

    @NotNull(message = "O valor limite é obrigatório.")
    @Positive(message = "O valor limite deve ser maior que zero.")
    private Double valorLimite; // Ex: 450.00

    @NotBlank(message = "O período (mês/ano) é obrigatório.")
    @Size(max = 10, message = "O período deve seguir o formato padrão (ex: 07/2026).")
    private String mesAno; // Armazena o mês e ano alvo

    @NotNull(message = "O ID do usuário é obrigatório.")
    private UUID usuarioId;

    @NotNull(message = "O ID da categoria é obrigatório.")
    private UUID categoriaId; 
}