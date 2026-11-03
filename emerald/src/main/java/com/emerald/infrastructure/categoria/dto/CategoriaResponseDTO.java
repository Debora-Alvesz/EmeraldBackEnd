package com.emerald.infrastructure.categoria.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CategoriaResponseDTO {
    private UUID id;
    private String nome;
    private String tipo;
    private String descricao;
    private String cor;
    private UUID usuarioId; // Retorna apenas o ID do dono da categoria
}