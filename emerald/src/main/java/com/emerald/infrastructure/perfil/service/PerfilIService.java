package com.emerald.infrastructure.perfil.service;

import com.emerald.infrastructure.perfil.dto.PerfilRequestDTO;
import com.emerald.infrastructure.perfil.dto.PerfilResponseDTO;

import java.util.List;

public interface PerfilIService {

    // Realiza a criação e persistência de um novo perfil de acesso.
    PerfilResponseDTO save(PerfilRequestDTO request);

    // Recupera a listagem de todos os perfis parametrizados no sistema.
    List<PerfilResponseDTO> findAll();

    // Busca as propriedades de um perfil específico por meio de seu ID único.
    PerfilResponseDTO findById(Long id);

    // Atualiza a nomenclatura ou dados de um perfil de acesso existente.
    PerfilResponseDTO update(Long id, PerfilRequestDTO request);

    // Remove fisicamente o registro de perfil do banco de dados.
    void delete(Long id);

    // Mapeia e retorna as permissões e autorizações vinculadas ao perfil.
    List<String> obterPermissoes(Long id);
}