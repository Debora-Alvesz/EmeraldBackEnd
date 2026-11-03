package com.emerald.infrastructure.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoriaRequestDTO {

    @NotBlank(message = "O nome da categoria é obrigatório.")
    @Size(max = 50, message = "O nome da categoria não pode passar de 50 caracteres.")
    private String nome; // Ex: "Alimentação", "Salário"

    @NotBlank(message = "O tipo da categoria é obrigatório.")
    @Size(max = 20, message = "O tipo da categoria não pode passar de 20 caracteres.")
    private String tipo; // Aceita estritamente: "RECEITA" ou "DESPESA"

    @Size(max = 255, message = "A descrição não pode passar de 255 caracteres.")
    private String descricao;

    @Size(max = 7, message = "A cor não pode passar de 7 caracteres.")
    private String cor;

    @NotNull(message = "O ID do usuário proprietário é obrigatório.")
    private UUID usuarioId; // Vincula a categoria ao usuário para garantir o isolamento
}