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
@RequiredArgsConstructor // Injeta automaticamente os repositórios e o mapper via construtor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO request) {
        // Regra de Negócio: Valida se o usuário dono desta nova categoria realmente existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID fornecido."));

        Categoria categoria = categoriaMapper.toEntity(request);
        categoria.setUsuario(usuario); // Vincula o usuário de forma segura no ecossistema do backend

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDto(categoriaSalva);
    }

    // Retorna apenas as categorias que pertencem ao usuário logado (Isolamento total)
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> buscarPorUsuario(UUID usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId).stream()
                .map(categoriaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));
        return categoriaMapper.toResponseDto(categoria);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(UUID id, CategoriaRequestDTO request) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));

        // Atualiza os campos permitidos da categoria
        categoriaExistente.setNome(request.getNome());
        categoriaExistente.setTipo(request.getTipo().toUpperCase());

        Categoria categoriaAtualizada = categoriaRepository.save(categoriaExistente);
        return categoriaMapper.toResponseDto(categoriaAtualizada);
    }

    @Transactional
    public void excluir(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));

        // No futuro, se houver transações vinculadas a esta categoria, o banco impedirá a exclusão física (Integridade Referencial)
        categoriaRepository.delete(categoria);
    }
}