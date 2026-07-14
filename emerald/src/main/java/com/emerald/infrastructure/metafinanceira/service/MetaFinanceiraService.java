package com.emerald.infrastructure.metafinanceira.service;

import com.emerald.infrastructure.categoria.entity.Categoria;
import com.emerald.infrastructure.categoria.repository.CategoriaRepository;
import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraRequestDTO;
import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraResponseDTO;
import com.emerald.infrastructure.metafinanceira.entity.MetaFinanceira;
import com.emerald.infrastructure.metafinanceira.mapper.MetaFinanceiraMapper;
import com.emerald.infrastructure.metafinanceira.repository.MetaFinanceiraRepository;
import com.emerald.infrastructure.usuario.entity.Usuario;
import com.emerald.infrastructure.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetaFinanceiraService implements MetaFinanceiraIService {

    private final MetaFinanceiraRepository metaFinanceiraRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetaFinanceiraMapper metaFinanceiraMapper;

    @Override
    @Transactional
    public MetaFinanceiraResponseDTO save(MetaFinanceiraRequestDTO request) {
        // Valida a existência do usuário associado à nova meta financeira.
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        // Valida a existência da categoria associada à nova meta financeira.
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID fornecido."));

        MetaFinanceira meta = metaFinanceiraMapper.toEntity(request);
        meta.setUsuario(usuario);
        meta.setCategoria(categoria);

        MetaFinanceira metaSalva = metaFinanceiraRepository.save(meta);
        return metaFinanceiraMapper.toResponseDto(metaSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaFinanceiraResponseDTO> findByUsuarioId(UUID usuarioId) {
        // Recupera todas as metas financeiras pertencentes a um determinado usuário.
        return metaFinanceiraRepository.findByUsuarioId(usuarioId).stream()
                .map(metaFinanceiraMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaFinanceiraResponseDTO> findByUsuarioIdAndMesAno(UUID usuarioId, String mesAno) {
        // Filtra o planejamento de metas de um usuário baseado em um período específico.
        return metaFinanceiraRepository.findByUsuarioIdAndMesAno(usuarioId, mesAno).stream()
                .map(metaFinanceiraMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MetaFinanceiraResponseDTO findById(UUID id, UUID usuarioId) {
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta financeira não encontrada com o ID: " + id));

        // Verifica a titularidade da meta para evitar acesso cruzado indevido entre usuários.
        if (!meta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para acessar esta meta.");
        }

        return metaFinanceiraMapper.toResponseDto(meta);
    }

    @Override
    @Transactional
    public MetaFinanceiraResponseDTO update(UUID id, UUID usuarioId, MetaFinanceiraRequestDTO request) {
        MetaFinanceira metaExistente = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta financeira não encontrada com o ID: " + id));

        // Valida a propriedade da meta antes de autorizar modificações cadastrais.
        if (!metaExistente.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para alterar esta meta.");
        }

        Categoria novaCategoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID fornecido."));

        metaExistente.setValorLimite(request.getValorLimite());
        metaExistente.setMesAno(request.getMesAno());
        metaExistente.setCategoria(novaCategoria);

        MetaFinanceira metaAtualizada = metaFinanceiraRepository.save(metaExistente);
        return metaFinanceiraMapper.toResponseDto(metaAtualizada);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta financeira não encontrada com o ID: " + id));

        // Restringe a operação de remoção física apenas ao usuário proprietário do registro.
        if (!meta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para excluir esta meta.");
        }

        metaFinanceiraRepository.delete(meta);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularProgressoDaMeta(UUID id, UUID usuarioId) {
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta não encontrada."));

        // Protege a leitura de dados estratégicos contra acessos externos maliciosos.
        if (!meta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para calcular dados desta meta.");
        }

        // TODO: Implementação pendente até a conclusão e integração do módulo de Transações.
        Double totalGasto = 0.0;

        if (meta.getValorLimite() == 0) return 0.0;
        return (totalGasto / meta.getValorLimite()) * 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public String emitirAlertaDeEstouro(UUID id, UUID usuarioId) {
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta não encontrada."));

        // Protege a leitura de dados estratégicos contra acessos externos maliciosos.
        if (!meta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Permissão insuficiente para verificar dados desta meta.");
        }

        // TODO: Implementação pendente até a conclusão e integração do módulo de Transações.
        Double totalGasto = 0.0;

        if (totalGasto > meta.getValorLimite()) {
            return "ALERTA DE ESTOURO! Limite ultrapassado na categoria: " + meta.getCategoria().getNome();
        }

        return "Meta dentro do planejado. Limite: R$ " + meta.getValorLimite();
    }
}