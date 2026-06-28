package com.emerald.infrastructure.contabancaria.service;

import com.emerald.infrastructure.contabancaria.dto.ContaBancariaRequestDTO;
import com.emerald.infrastructure.contabancaria.dto.ContaBancariaResponseDTO;
import com.emerald.infrastructure.contabancaria.entity.ContaBancaria;
import com.emerald.infrastructure.contabancaria.mapper.ContaBancariaMapper;
import com.emerald.infrastructure.contabancaria.repository.ContaBancariaRepository;
import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.usuario.entity.Usuario;
import com.emerald.infrastructure.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaBancariaService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContaBancariaMapper contaBancariaMapper;

    @Transactional
    public ContaBancariaResponseDTO criar(ContaBancariaRequestDTO request) {
        // Valida se o usuário dono da conta realmente existe no sistema
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        ContaBancaria conta = contaBancariaMapper.toEntity(request);
        conta.setUsuario(usuario); // Vincula a entidade Usuário de forma segura no backend

        ContaBancaria contaSalva = contaBancariaRepository.save(conta);
        return contaBancariaMapper.toResponseDto(contaSalva);
    }

    // Busca todas as contas filtrando estritamente pelo ID do usuário (Garante o isolamento)
    @Transactional(readOnly = true)
    public List<ContaBancariaResponseDTO> buscarPorUsuario(UUID usuarioId) {
        return contaBancariaRepository.findByUsuarioId(usuarioId).stream()
                .map(contaBancariaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContaBancariaResponseDTO buscarPorId(UUID id) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada com o ID: " + id));
        return contaBancariaMapper.toResponseDto(conta);
    }

    @Transactional
    public ContaBancariaResponseDTO atualizar(UUID id, ContaBancariaRequestDTO request) {
        ContaBancaria contaExistente = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada com o ID: " + id));

        contaExistente.setNomeConta(request.getNomeConta());
        contaExistente.setTipoConta(request.getTipoConta());
        // O saldo geralmente não se atualiza direto pelo PUT, mas mantemos para o CRUD inicial
        contaExistente.setSaldo(request.getSaldo());

        ContaBancaria contaAtualizada = contaBancariaRepository.save(contaExistente);
        return contaBancariaMapper.toResponseDto(contaAtualizada);
    }

    /**
     * Método do diagrama (+ inativarConta)
     * Por enquanto realiza a exclusão física do registro. Caso adicione um campo "ativo", vira exclusão lógica.
     */
    @Transactional
    public void inativarConta(UUID id) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada com o ID: " + id));
        contaBancariaRepository.delete(conta);
    }

    /**
     * Método do diagrama (+ atualizarSaldo)
     * Este método será chamado automaticamente pelo sistema sempre que uma transação (receita/despesa) for criada.
     */
    @Transactional
    public void atualizarSaldo(UUID id, BigDecimal valor, String tipoTransacao) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada para atualização de saldo."));

        if ("RECEITA".equalsIgnoreCase(tipoTransacao)) {
            conta.setSaldo(conta.getSaldo().add(valor)); // Soma se for receita
        } else if ("DESPESA".equalsIgnoreCase(tipoTransacao)) {
            conta.setSaldo(conta.getSaldo().subtract(valor)); // Subtrai se for despesa
        } else {
            throw new BusinessException("Tipo de transação inválido para atualizar o saldo.");
        }

        contaBancariaRepository.save(conta);
    }

    /**
     * Método do diagrama (+ obterExtratoMensal)
     * Realiza a preparação estrutural para a emissão do extrato consolidado da conta.
     * Nesta etapa inicial, valida a existência da conta informada e retorna o escopo
     * de dados formatado (atualmente inicializado como uma lista limpa de registros).
     */
    @Transactional(readOnly = true)
    public List<String> obterExtratoMensal(UUID id, Integer mes, Integer ano) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada para gerar extrato."));

        // TODO: Na entrega 2, buscar as transações desta conta filtradas por mês/ano
        return new ArrayList<>();
    }
}