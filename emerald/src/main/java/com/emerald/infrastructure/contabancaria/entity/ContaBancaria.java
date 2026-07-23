package com.emerald.infrastructure.contabancaria.entity;

import com.emerald.infrastructure.contabancaria.entity.enums.TipoConta;
import com.emerald.infrastructure.usuario.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @GeneratedValue(strategy = GenerationType.UUID) // Gera automaticamente o UUID
    private UUID id;

    @Column(nullable = false, length = 50) // Nome descritivo (ex: "Nubank", "Itaú")
    private String nomeConta;

    @Column(nullable = false) // Saldo atual da conta
    private BigDecimal saldo;

    //Usando o Enum e avisando o JPA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoConta tipoConta;

    @ManyToOne // Muitas contas bancárias pertencem a um único Usuário
    @JoinColumn(name = "usuario_id", nullable = false) // Chave Estrangeira (FK)
    private Usuario usuario;
}