package com.emerald.infrastructure.metodopagamento.service;

import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoRequestDTO;
import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoResponseDTO;

import java.util.List;

public interface MetodoPagamentoIService {

    // Realiza o cadastro e a persistência de um novo método de pagamento no sistema.
    MetodoPagamentoResponseDTO save(MetodoPagamentoRequestDTO request);

    // Recupera a listagem completa de todos os métodos de pagamento cadastrados.
    List<MetodoPagamentoResponseDTO> findAll();

    // Busca um método de pagamento específico por meio de seu ID único.
    MetodoPagamentoResponseDTO findById(Long id);

    // Atualiza a nomenclatura de um método de pagamento existente.
    MetodoPagamentoResponseDTO update(Long id, MetodoPagamentoRequestDTO request);

    // Remove fisicamente o registro do método de pagamento do banco de dados.
    void delete(Long id);
}