package com.emerald.infrastructure.categoria.service;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoriaIService {

    // Realiza a persistência de uma nova categoria vinculada a um usuário.
    CategoriaResponseDTO save(CategoriaRequestDTO request);

    // Retorna a lista de categorias associadas a um identificador de usuário específico.
    List<CategoriaResponseDTO> findByUsuarioId(UUID usuarioId);

    // Busca uma categoria específica validando a propriedade para evitar IDOR.
    CategoriaResponseDTO findById(UUID id, UUID usuarioId);

    // Atualiza os dados de uma categoria existente se o usuário for o proprietário.
    CategoriaResponseDTO update(UUID id, UUID usuarioId, CategoriaRequestDTO request);

    // Remove fisicamente o registro de uma categoria após validação de propriedade.
    void delete(UUID id, UUID usuarioId);
}