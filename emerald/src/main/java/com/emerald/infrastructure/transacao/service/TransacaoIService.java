package com.emerald.infrastructure.transacao.service;

import com.emerald.infrastructure.transacao.dto.TransacaoRequestDTO;
import com.emerald.infrastructure.transacao.dto.TransacaoResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TransacaoIService {

    // Registra e processa uma nova transação financeira, atualizando o saldo da conta associada.
    TransacaoResponseDTO save(TransacaoRequestDTO request);

    // Recupera os detalhes de uma transação específica por meio do seu ID.
    TransacaoResponseDTO findById(UUID id);

    // Retorna todas as transações cadastradas para um usuário específico.
    List<TransacaoResponseDTO> findByUsuario(UUID usuarioId);

    // Filtra as transações de um usuário de acordo com um intervalo de datas.
    List<TransacaoResponseDTO> filtrarPorPeriodo(UUID usuarioId, Date dataInicio, Date dataFim);

    // Remove uma transação do sistema e estorna o seu valor no saldo da conta correspondente.
    void delete(UUID id);
}