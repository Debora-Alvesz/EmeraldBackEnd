package com.emerald.infrastructure.usuario.entity;

import com.emerald.infrastructure.perfil.entity.Perfil;
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

import java.util.UUID;

@Entity
@Table(name = "tb_usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gera automaticamente o UUID no padrão correto
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100) // unique=true garante que não haja e-mails duplicados no banco
    private String email;

    @Column(nullable = false, length = 255) // Comprimento maior prevendo o hash da senha no futuro
    private String senha;

    @ManyToOne // Muitos usuários possuem o mesmo Perfil
    @JoinColumn(name = "perfil_id", nullable = false) // Cria a FK (Chave Estrangeira) na tabela de usuários
    private Perfil perfil;
} 