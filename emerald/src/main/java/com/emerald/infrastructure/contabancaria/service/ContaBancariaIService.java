package com.emerald.infrastructure.contabancaria.service;

import com.emerald.infrastructure.contabancaria.dto.ContaBancariaRequestDTO;
import com.emerald.infrastructure.contabancaria.dto.ContaBancariaResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ContaBancariaIService {

    // Realiza o cadastro e persistência de uma nova conta bancária.
    ContaBancariaResponseDTO save(ContaBancariaRequestDTO request);

    // Lista todas as contas bancárias associadas a um ID de usuário específico.
    List<ContaBancariaResponseDTO> findByUsuarioId(UUID usuarioId);

    // Busca uma conta específica validando a propriedade para evitar IDOR.
    ContaBancariaResponseDTO findById(UUID id, UUID usuarioId);

    // Atualiza os dados cadastrais da conta se o usuário for o proprietário.
    ContaBancariaResponseDTO update(UUID id, UUID usuarioId, ContaBancariaRequestDTO request);

    // Remove a conta bancária do banco de dados após validação de propriedade.
    void delete(UUID id, UUID usuarioId);

    // Atualiza o saldo da conta dinamicamente ao processar receitas ou despesas.
    void atualizarSaldo(UUID id, BigDecimal valor, String tipoTransacao);

    // Gera um resumo ou extrato das movimentações mensais da conta.
    List<String> obterExtratoMensal(UUID id, UUID usuarioId, Integer mes, Integer ano);
}