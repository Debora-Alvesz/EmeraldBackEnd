package com.emerald.infrastructure.usuario.service;

import com.emerald.infrastructure.usuario.dto.LoginRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UsuarioIService {

    // Realiza a persistência de um novo usuário associado ao perfil básico do sistema.
    UsuarioResponseDTO save(UsuarioRequestDTO request);

    // Processa a validação cadastral para autenticação e login de usuários.
    UsuarioResponseDTO autenticar(LoginRequestDTO loginRequest);

    // Recupera a listagem completa de todos os usuários registrados no sistema.
    List<UsuarioResponseDTO> findAll();

    // Localiza e retorna as propriedades de um usuário específico por meio do ID.
    UsuarioResponseDTO findById(UUID id);

    // Atualiza as informações de login e dados cadastrais de um usuário existente.
    UsuarioResponseDTO update(UUID id, UsuarioRequestDTO request);

    // Remove fisicamente o registro do usuário do banco de dados do sistema.
    void delete(UUID id);
}