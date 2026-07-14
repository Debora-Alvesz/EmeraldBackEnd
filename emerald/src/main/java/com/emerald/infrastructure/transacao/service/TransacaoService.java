package com.emerald.infrastructure.transacao.service;

import com.emerald.infrastructure.categoria.entity.Categoria;
import com.emerald.infrastructure.categoria.repository.CategoriaRepository;
import com.emerald.infrastructure.contabancaria.entity.ContaBancaria;
import com.emerald.infrastructure.contabancaria.repository.ContaBancariaRepository;
import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.metodopagamento.entity.MetodoPagamento;
import com.emerald.infrastructure.metodopagamento.repository.MetodoPagamentoRepository;
import com.emerald.infrastructure.transacao.dto.TransacaoRequestDTO;
import com.emerald.infrastructure.transacao.dto.TransacaoResponseDTO;
import com.emerald.infrastructure.transacao.entity.Transacao;
import com.emerald.infrastructure.transacao.mapper.TransacaoMapper;
import com.emerald.infrastructure.transacao.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService implements TransacaoIService {

    private final TransacaoRepository transacaoRepository;
    private final ContaBancariaRepository contaBancariaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetodoPagamentoRepository metodoPagamentoRepository;
    private final TransacaoMapper transacaoMapper;

    @Override
    @Transactional
    public TransacaoResponseDTO save(TransacaoRequestDTO request) {
        // Valida a existência da conta bancária informada
        ContaBancaria conta = contaBancariaRepository.findById(request.getContaBancariaId())
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada para o ID: " + request.getContaBancariaId()));

        // Valida a existência da categoria informada
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada para o ID: " + request.getCategoriaId()));

        // Valida a existência do método de pagamento informado
        MetodoPagamento metodo = metodoPagamentoRepository.findById(request.getMetodoPagamentoId())
                .orElseThrow(() -> new BusinessException("Método de pagamento não encontrado para o ID: " + request.getMetodoPagamentoId()));

        // Converte o DTO para entidade e associa os relacionamentos validados
        Transacao transacao = transacaoMapper.toEntity(request);
        transacao.setContaBancaria(conta);
        transacao.setCategoria(categoria);
        transacao.setMetodoPagamento(metodo);

        // Processa a efetivação física da transação no saldo da conta
        efetivarTransacao(conta, transacao.getValor());

        Transacao transacaoSalva = transacaoRepository.save(transacao);
        return transacaoMapper.toResponseDto(transacaoSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public TransacaoResponseDTO findById(UUID id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Transação não encontrada para o ID: " + id));
        return transacaoMapper.toResponseDto(transacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> findByUsuario(UUID usuarioId) {
        return transacaoRepository.findByContaBancariaUsuarioId(usuarioId).stream()
                .map(transacaoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> filtrarPorPeriodo(UUID usuarioId, Date dataInicio, Date dataFim) {
        if (dataInicio.after(dataFim)) {
            throw new BusinessException("A data de início do filtro não pode ser posterior à data de fim.");
        }
        return transacaoRepository.findByContaBancariaUsuarioIdAndDataBetween(usuarioId, dataInicio, dataFim).stream()
                .map(transacaoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Transação não encontrada para o ID: " + id));

        // Realiza o estorno do valor na conta antes de deletar o registro físico
        estornarTransacao(transacao.getContaBancaria(), transacao.getValor());

        transacaoRepository.delete(transacao);
    }

    // Atualiza o saldo somando o valor recebido (soma tanto receitas positivas quanto despesas negativas)
    private void efetivarTransacao(ContaBancaria conta, Double valor) {
        BigDecimal valorTransacao = BigDecimal.valueOf(valor);
        BigDecimal novoSaldo = conta.getSaldo().add(valorTransacao);
        conta.setSaldo(novoSaldo);
        contaBancariaRepository.save(conta);
    }

    // Reverte o impacto financeiro de uma transação excluída subtraindo seu valor do saldo da conta
    private void estornarTransacao(ContaBancaria conta, Double valor) {
        BigDecimal valorTransacao = BigDecimal.valueOf(valor);
        BigDecimal novoSaldo = conta.getSaldo().subtract(valorTransacao);
        conta.setSaldo(novoSaldo);
        contaBancariaRepository.save(conta);
    }
}