package com.emerald.infrastructure.metodopagamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetodoPagamentoRequestDTO {

    @NotBlank(message = "O nome do método de pagamento é obrigatório.")
    @Size(max = 100, message = "O nome do método deve ter no máximo 100 caracteres.")
    private String nomeMetodo; // Nome da forma de pagamento (ex: "Pix", "Crédito")
}