package com.emerald.infrastructure.metafinanceira.repository;

import com.emerald.infrastructure.metafinanceira.entity.MetaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetaFinanceiraRepository extends JpaRepository<MetaFinanceira, UUID> {

    // Busca todas as metas financeiras associadas a um usuário específico.

    List<MetaFinanceira> findByUsuarioId(UUID usuarioId);

    //Busca as metas de um usuário para um mês/ano específico (ex: "07/2026").
    
    List<MetaFinanceira> findByUsuarioIdAndMesAno(UUID usuarioId, String mesAno);
}