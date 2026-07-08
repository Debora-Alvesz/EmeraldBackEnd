package com.emerald.infrastructure.categoria.repository;

import com.emerald.infrastructure.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

// Busca todas as categorias vinculadas a um determinado usuário
// Utilizado para garantir isolamento de dados (cada usuário acessa apenas suas categorias)
    List<Categoria> findByUsuarioId(UUID usuarioId);
}
