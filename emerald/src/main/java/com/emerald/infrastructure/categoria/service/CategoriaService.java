package com.emerald.infrastructure.categoria.service;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;
import com.emerald.infrastructure.categoria.entity.Categoria;
import com.emerald.infrastructure.categoria.mapper.CategoriaMapper;
import com.emerald.infrastructure.categoria.repository.CategoriaRepository;
import com.emerald.infrastructure.exception.BusinessException;
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
public class CategoriaService implements CategoriaIService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional
    public CategoriaResponseDTO save(CategoriaRequestDTO request) {
        if (request.getUsuarioId() == null) {
            throw new BusinessException("O ID do usuário é obrigatório.");
        }

        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new BusinessException("O nome da categoria é obrigatório.");
        }

        if (request.getTipo() == null || request.getTipo().isBlank()) {
            throw new BusinessException("O tipo da categoria é obrigatório.");
        }

        String tipoUpper = request.getTipo().toUpperCase().trim();
        if (!tipoUpper.equals("RECEITA") && !tipoUpper.equals("DESPESA")) {
            throw new BusinessException("O tipo da categoria deve ser 'RECEITA' ou 'DESPESA'.");
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        Categoria categoria = categoriaMapper.toEntity(request);
        categoria.setTipo(tipoUpper);
        categoria.setUsuario(usuario);

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDto(categoriaSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findByUsuarioId(UUID usuarioId) {
        if (usuarioId == null) {
            throw new BusinessException("O ID do usuário não pode ser nulo.");
        }

        return categoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(categoriaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(UUID id, UUID usuarioId) {
        if (id == null || usuarioId == null) {
            throw new BusinessException("Os identificadores informados não podem ser nulos.");
        }

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));

        // Valida propriedade do recurso para mitigar vulnerabilidade IDOR.
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para acessar esta categoria.");
        }

        return categoriaMapper.toResponseDto(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO update(UUID id, UUID usuarioId, CategoriaRequestDTO request) {
        if (id == null || usuarioId == null) {
            throw new BusinessException("Os identificadores informados não podem ser nulos.");
        }

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));

        // Impede que usuários modifiquem dados de propriedade alheia.
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para alterar esta categoria.");
        }

        String tipoUpper = request.getTipo().toUpperCase().trim();
        if (!tipoUpper.equals("RECEITA") && !tipoUpper.equals("DESPESA")) {
            throw new BusinessException("O tipo da categoria deve ser 'RECEITA' ou 'DESPESA'.");
        }

        categoria.setNome(request.getNome());
        categoria.setTipo(tipoUpper);

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDto(categoriaAtualizada);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        if (id == null || usuarioId == null) {
            throw new BusinessException("Os identificadores informados não podem ser nulos.");
        }

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));

        // Garante o isolamento físico permitindo a remoção apenas pelo proprietário do registro.
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado: Você não tem permissão para excluir esta categoria.");
        }

        categoriaRepository.delete(categoria);
    }
}