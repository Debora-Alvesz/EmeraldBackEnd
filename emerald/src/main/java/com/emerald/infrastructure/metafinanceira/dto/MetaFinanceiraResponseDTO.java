package com.emerald.infrastructure.metafinanceira.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class MetaFinanceiraResponseDTO {
    private UUID id;
    private Double valorLimite;
    private String mesAno;
    private UUID usuarioId;
    private UUID categoriaId;
    private String nomeCategoria; // Campo facilitador para o React exibir o nome (ex: "Alimentação") na tela de metas
}