package com.emerald.infrastructure.perfil.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "perfil") // Define explicitamente o nome da tabela no PostgreSQL do Supabase
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* O 'nomePerfil' armazenará estritamente as strings "ADMIN" ou "USER".
     * Definimos como 'unique' para o banco nunca permitir duplicar esses perfis,
     * e 'nullable = false' para garantir que todo perfil criado tenha uma função.
     */
    @Column(nullable = false, unique = true)
    private String nomePerfil;
}