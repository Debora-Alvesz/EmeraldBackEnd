package com.emerald.infrastructure.contabancaria.service;

import com.emerald.infrastructure.contabancaria.dto.ContaBancariaRequestDTO;
import com.emerald.infrastructure.contabancaria.dto.ContaBancariaResponseDTO;
import com.emerald.infrastructure.contabancaria.entity.ContaBancaria;
import com.emerald.infrastructure.contabancaria.mapper.ContaBancariaMapper;
import com.emerald.infrastructure.contabancaria.repository.ContaBancariaRepository;
import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.transacao.dto.TransacaoResponseDTO;
import com.emerald.infrastructure.transacao.entity.Transacao;
import com.emerald.infrastructure.transacao.mapper.TransacaoMapper;
import com.emerald.infrastructure.transacao.repository.TransacaoRepository;
import com.emerald.infrastructure.usuario.entity.Usuario;
import com.emerald.infrastructure.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaBancariaService implements ContaBancariaIService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContaBancariaMapper contaBancariaMapper;

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Override
    @Transactional
    public ContaBancariaResponseDTO save(ContaBancariaRequestDTO request) {
        // Valida se o usuário existe antes de associar a nova conta bancária
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        ContaBancaria conta = contaBancariaMapper.toEntity(request);
        conta.setUsuario(usuario);

        ContaBancaria contaSalva = contaBancariaRepository.save(conta);
        return contaBancariaMapper.toResponseDto(contaSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaBancariaResponseDTO> findByUsuarioId(UUID usuarioId) {
        // Utiliza Java Streams para mapear a lista de entidades para uma lista de DTOs de resposta
        return contaBancariaRepository.findByUsuarioId(usuarioId).stream()
                .map(contaBancariaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContaBancariaResponseDTO findById(UUID id, UUID usuarioId) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Proteção contra IDOR: Garante que o recurso pertence estritamente ao usuário autenticado/solicitante
        if (!conta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para acessar esta conta.");
        }

        return contaBancariaMapper.toResponseDto(conta);
    }

    @Override
    @Transactional
    public ContaBancariaResponseDTO update(UUID id, UUID usuarioId, ContaBancariaRequestDTO request) {
        ContaBancaria contaExistente = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Validação de segurança para impedir modificações em contas de terceiros
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

    @Override
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // Verifica a propriedade do registro antes de autorizar a exclusão física no banco de dados
        if (!conta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para excluir esta conta.");
        }

        contaBancariaRepository.delete(conta);
    }

    @Override
    @Transactional
    public void atualizarSaldo(UUID id, BigDecimal valor, String tipoTransacao) {
        if (valor == null || tipoTransacao == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Dados inválidos para atualização de saldo.");
        }

        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada para atualização de saldo."));

        // Normaliza a string do tipo para evitar erros por letras maiúsculas/minúsculas ou espaços
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

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> obterExtratoMensal(UUID id, UUID usuarioId, Integer mes, Integer ano) {
        // 1. Valida se a conta bancária existe
        ContaBancaria conta = contaBancariaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta bancária não encontrada."));

        // 2. Trava de segurança para impedir vazamento de dados entre usuários
        if (!conta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para extrair dados desta conta.");
        }

        // 3. Define o primeiro e o último dia do mês/ano informados
        LocalDate primeiroDia = LocalDate.of(ano, mes, 1);
        LocalDate ultimoDia = primeiroDia.withDayOfMonth(primeiroDia.lengthOfMonth());

        // Converte para java.util.Date compatível com a entidade Transacao
        Date inicio = Date.from(primeiroDia.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fim = Date.from(ultimoDia.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        // 4. Busca usando o método nativo do TransacaoRepository
        List<Transacao> transacoes = transacaoRepository.findByContaBancariaIdAndDataBetween(id, inicio, fim);

        // 5. Converte a lista de entidades para lista de DTOs utilizando o mapper correto
        return transacoes.stream()
                .map(transacaoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}