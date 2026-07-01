package com.emerald.infrastructure.categoria.repository;

import com.emerald.infrastructure.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    /**
     * Busca todas as categorias associadas a um usuário específico.
     * Garante que um usuário nunca acesse ou
     * utilize as categorias personalizadas de outro usuário do sistema.
     */
    List<Categoria> findByUsuarioId(UUID usuarioId);
}