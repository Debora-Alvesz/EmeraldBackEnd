package com.emerald.infrastructure.transacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TransacaoRequestDTO {

    @NotBlank(message = "A descrição da transação é obrigatória.")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String descricao; // Descrição ou título identificador da transação

    @NotNull(message = "O valor da transação é obrigatório.")
    private Double valor; // Valor da movimentação (positivo para receitas, negativo para despesas)

    @NotNull(message = "A data da transação é obrigatória.")
    private Date data; // Data de realização da movimentação financeira

    @NotNull(message = "O ID da conta bancária é obrigatório.")
    private UUID contaBancariaId; // Identificador da conta onde o saldo será alterado

    @NotNull(message = "O ID da categoria é obrigatório.")
    private UUID categoriaId; // Identificador da categoria para fins de classificação

    @NotNull(message = "O ID do método de pagamento é obrigatório.")
    private Long metodoPagamentoId; // Identificador do método de pagamento utilizado
}