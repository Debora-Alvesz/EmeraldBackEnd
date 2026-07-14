package com.emerald.infrastructure.metodopagamento.repository;

import com.emerald.infrastructure.metodopagamento.entity.MetodoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamento, Long> {

    // Busca um método de pagamento específico pelo nome (ex: "Pix").

    Optional<MetodoPagamento> findByNomeMetodo(String nomeMetodo);
}