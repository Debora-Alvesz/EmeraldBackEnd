package com.emerald.infrastructure.contabancaria.repository;

import com.emerald.infrastructure.contabancaria.entity.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, UUID> {
    
    List<ContaBancaria> findByUsuarioId(UUID usuarioId);
}