package com.emerald.infrastructure.metafinanceira.service;

import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraRequestDTO;
import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MetaFinanceiraIService {

    // Realiza o cadastro e persistência de uma nova meta financeira.
    MetaFinanceiraResponseDTO save(MetaFinanceiraRequestDTO request);

    // Recupera todas as metas financeiras pertencentes a um determinado usuário.
    List<MetaFinanceiraResponseDTO> findByUsuarioId(UUID usuarioId);

    // Filtra o planejamento de metas de um usuário baseado em um período (Mês/Ano) específico.
    List<MetaFinanceiraResponseDTO> findByUsuarioIdAndMesAno(UUID usuarioId, String mesAno);

    // Busca uma meta específica validando a propriedade para evitar acessos indevidos.
    MetaFinanceiraResponseDTO findById(UUID id, UUID usuarioId);

    // Atualiza os dados cadastrais da meta se o usuário for o proprietário.
    MetaFinanceiraResponseDTO update(UUID id, UUID usuarioId, MetaFinanceiraRequestDTO request);

    // Remove a meta financeira do banco de dados após validação de propriedade.
    void delete(UUID id, UUID usuarioId);

    // Calcula o percentual de uso do orçamento definido na meta (aguarda módulo de transações).
    Double calcularProgressoDaMeta(UUID id, UUID usuarioId);

    // Verifica se os gastos ultrapassaram o limite da meta e emite um alerta.
    String emitirAlertaDeEstouro(UUID id, UUID usuarioId);
}