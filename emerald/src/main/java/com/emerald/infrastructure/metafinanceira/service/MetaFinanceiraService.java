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
public class MetaFinanceiraService {

    private final MetaFinanceiraRepository metaFinanceiraRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetaFinanceiraMapper metaFinanceiraMapper;

    @Transactional
    public MetaFinanceiraResponseDTO criar(MetaFinanceiraRequestDTO request) {
        // Valida se o usuário e a categoria existem antes de salvar a nova meta
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID fornecido."));

        MetaFinanceira meta = metaFinanceiraMapper.toEntity(request);
        meta.setUsuario(usuario);
        meta.setCategoria(categoria);

        MetaFinanceira metaSalva = metaFinanceiraRepository.save(meta);
        return metaFinanceiraMapper.toResponseDto(metaSalva);
    }

    @Transactional(readOnly = true)
    public List<MetaFinanceiraResponseDTO> buscarPorUsuario(UUID usuarioId) {
        // Recupera todas as metas cadastradas para um usuário específico
        return metaFinanceiraRepository.findByUsuarioId(usuarioId).stream()
                .map(metaFinanceiraMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MetaFinanceiraResponseDTO> buscarPorUsuarioEPeriodo(UUID usuarioId, String mesAno) {
        // Filtra as metas de um usuário por um mês e ano específicos (ex: "07/2026")
        return metaFinanceiraRepository.findByUsuarioIdAndMesAno(usuarioId, mesAno).stream()
                .map(metaFinanceiraMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MetaFinanceiraResponseDTO buscarPorId(UUID id) {
        // Busca os detalhes de uma meta específica através do seu ID único
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta financeira não encontrada com o ID: " + id));
        return metaFinanceiraMapper.toResponseDto(meta);
    }

    @Transactional
    public MetaFinanceiraResponseDTO atualizar(UUID id, MetaFinanceiraRequestDTO request) {
        // Atualiza os valores limites e a categoria vinculada de uma meta existente
        MetaFinanceira metaExistente = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta financeira não encontrada com o ID: " + id));

        Categoria novaCategoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID fornecido."));

        metaExistente.setValorLimite(request.getValorLimite());
        metaExistente.setMesAno(request.getMesAno());
        metaExistente.setCategoria(novaCategoria);

        MetaFinanceira metaAtualizada = metaFinanceiraRepository.save(metaExistente);
        return metaFinanceiraMapper.toResponseDto(metaAtualizada);
    }

    @Transactional
    public void excluir(UUID id) {
        // Remove permanentemente uma meta do banco de dados pelo ID
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta financeira não encontrada com o ID: " + id));
        metaFinanceiraRepository.delete(meta);
    }

    // MÉTODO DO DIAGRAMA
    @Transactional(readOnly = true)
    public Double calcularProgressoDaMeta(UUID id) {
        // Calcula a porcentagem consumida do limite da meta (inicia em 0.0)
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta não encontrada."));

        // TODO: Buscar soma das parcelas da classe Transacao futuramente
        Double totalGasto = 0.0;

        if (meta.getValorLimite() == 0) return 0.0;
        return (totalGasto / meta.getValorLimite()) * 100.0;
    }

    // MÉTODO DO DIAGRAMA
    @Transactional(readOnly = true)
    public String emitirAlertaDeEstouro(UUID id) {
        // Verifica o teto de gastos e retorna uma mensagem de aviso caso ultrapasse o limite
        MetaFinanceira meta = metaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Meta não encontrada."));

        // TODO: Buscar soma das parcelas da classe Transacao futuramente
        Double totalGasto = 0.0;

        if (totalGasto > meta.getValorLimite()) {
            return "ALERTA DE ESTOURO! Limite ultrapassado na categoria: " + meta.getCategoria().getNome();
        }

        return "Meta dentro do planejado. Limite: R$ " + meta.getValorLimite();
    }
}