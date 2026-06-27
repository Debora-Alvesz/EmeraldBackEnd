package com.emerald.infrastructure.perfil.repository;

import com.emerald.infrastructure.perfil.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    // Método necessário para o Service verificar se o nome já existe
    Optional<Perfil> findByNomePerfil(String nomePerfil);
}