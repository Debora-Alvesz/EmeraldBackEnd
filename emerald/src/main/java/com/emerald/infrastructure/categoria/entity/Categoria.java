package com.emerald.infrastructure.categoria.entity;

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

import java.util.UUID;

@Entity
@Table(name = "tb_categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Mantém o padrão estável de UUID do projeto
    private UUID id;

    @Column(nullable = false, length = 50) // Nome da categoria (ex: "Alimentação", "Salário", "Lazer")
    private String nome;

    @Column(nullable = false, length = 20) // Tipo da categoria (Estritamente "RECEITA" ou "DESPESA")
    private String tipo;

    @ManyToOne // Muitas categorias pertencem a um único Usuário
    @JoinColumn(name = "usuario_id", nullable = false) // FK que isola as categorias por usuário
    private Usuario usuario;
}