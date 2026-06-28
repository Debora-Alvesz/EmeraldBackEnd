package com.emerald.infrastructure.contabancaria.entity;

import com.emerald.infrastructure.usuario.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_contas_bancarias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gera automaticamente o UUID padrão do projeto
    private UUID id;

    @Column(nullable = false, length = 50) // Nome descritivo (ex: "Nubank", "Carteira", "Poupança")
    private String nomeConta;

    @Column(nullable = false) // Saldo inicial ou saldo atual da conta
    private BigDecimal saldo;

    @Column(nullable = false, length = 30) // Tipo da conta (ex: "CORRENTE", "POUPANCA", "DINHEIRO")
    private String tipoConta;

    @ManyToOne // Muitas contas bancárias pertencem a um único Usuário
    @JoinColumn(name = "usuario_id", nullable = false) // Cria a FK (Chave Estrangeira) apontando para o usuário dono da conta
    private Usuario usuario;
}