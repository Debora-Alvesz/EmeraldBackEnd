package com.emerald.infrastructure.usuario.repository;

import com.emerald.infrastructure.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca um usuário pelo e-mail.
     * Esse método será a chave para:
     * 1. Validar se o e-mail já existe no cadastro (BusinessException).
     * 2. Buscar o usuário na hora de fazer o login (+ autenticar).
     */
    Optional<Usuario> findByEmail(String email);
}