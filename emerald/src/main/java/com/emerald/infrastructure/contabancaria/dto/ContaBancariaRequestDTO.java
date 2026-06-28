package com.emerald.infrastructure.contabancaria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para capturar os dados de criação ou atualização de uma conta bancária.
 */
@Data
public class ContaBancariaRequestDTO {

    @NotBlank(message = "O nome da conta é obrigatório.")
    @Size(max = 50, message = "O nome da conta não pode passar de 50 caracteres.")
    private String nomeConta;

    @NotNull(message = "O saldo inicial é obrigatório.")
    @PositiveOrZero(message = "O saldo inicial não pode ser negativo.")
    private BigDecimal saldo;

    @NotBlank(message = "O tipo da conta é obrigatório.")
    @Size(max = 30, message = "O tipo da conta não pode passar de 30 caracteres.")
    private String tipoConta; // Ex: CORRENTE, POUPANCA, DINHEIRO

    @NotNull(message = "O ID do usuário proprietário é obrigatório.")
    private UUID usuarioId; // Vincula a conta ao usuário dono
}