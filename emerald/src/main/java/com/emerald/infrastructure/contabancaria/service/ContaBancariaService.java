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
    public ContaBancariaResponseDTO save(ContaBancariaRequestDTO request) {
        // Valida se o usuário dono do vínculo realmente existe no banco de dados.
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        ContaBancaria conta = contaBancariaMapper.toEntity(request);
        conta.setUsuario(usuario);

        ContaBancaria contaSalva = contaBancariaRepository.save(conta);
        return contaBancariaMapper.toResponseDto(contaSalva);
    }

    @Transactional(readOnly = true)
    public List<ContaBancariaResponseDTO> findByUsuarioId(UUID usuarioId) {
        return contaBancariaRepository.findByUsuarioId(usuarioId).stream()
                .map(contaBancariaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContaBancariaResponseDTO findById(UUID id, UUID usuarioId) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Bloqueia o acesso se o ID do usuário logado não bater com o dono do registro.
        if (!conta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para acessar esta conta.");
        }

        return contaBancariaMapper.toResponseDto(conta);
    }

    @Transactional
    public ContaBancariaResponseDTO update(UUID id, UUID usuarioId, ContaBancariaRequestDTO request) {
        ContaBancaria contaExistente = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Bloqueia a alteração se o ID do usuário logado não for o dono do registro.
        if (!contaExistente.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para alterar esta conta.");
        }

        if (request.getNomeConta() != null && !request.getNomeConta().isBlank()) {
            contaExistente.setNomeConta(request.getNomeConta());
        }
        if (request.getTipoConta() != null && !request.getTipoConta().isBlank()) {
            contaExistente.setTipoConta(request.getTipoConta());
        }
        if (request.getSaldo() != null) {
            contaExistente.setSaldo(request.getSaldo());
        }

        ContaBancaria contaAtualizada = contaBancariaRepository.save(contaExistente);
        return contaBancariaMapper.toResponseDto(contaAtualizada);
    }

    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Bloqueia a exclusão se o ID do usuário logado não for o dono do registro.
        if (!conta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para excluir esta conta.");
        }

        contaBancariaRepository.delete(conta);
    }

    @Transactional
    public void atualizarSaldo(UUID id, BigDecimal valor, String tipoTransacao) {
        // Validação interna: impede cálculos ou mutações inconsistentes disparadas por transações.
        if (valor == null || tipoTransacao == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Dados inválidos para atualização de saldo.");
        }

        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada para atualização de saldo."));

        String tipoUpper = tipoTransacao.toUpperCase().trim();
        if ("RECEITA".equals(tipoUpper)) {
            conta.setSaldo(conta.getSaldo().add(valor));
        } else if ("DESPESA".equals(tipoUpper)) {
            conta.setSaldo(conta.getSaldo().subtract(valor));
        } else {
            throw new BusinessException("Tipo de transação inválido. Use RECEITA ou DESPESA.");
        }

        contaBancariaRepository.save(conta);
    }

    @Transactional(readOnly = true)
    public List<String> obterExtratoMensal(UUID id, UUID usuarioId, Integer mes, Integer ano) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Verifica a titularidade do registro antes de autorizar a geração do relatório.
        if (!conta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para extrair dados desta conta.");
        }

        // TODO: Implementação pendente até a conclusão e integração do módulo de Transações.
        return new ArrayList<>();
    }
}