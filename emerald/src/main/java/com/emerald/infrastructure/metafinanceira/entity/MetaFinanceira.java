package com.emerald.infrastructure.metafinanceira.entity;

import com.emerald.infrastructure.categoria.entity.Categoria;
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
@Table(name = "tb_metas_financeiras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "valor_limite")
    private Double valorLimite; // Teto máximo de gastos (ex: 500.00)

    @Column(nullable = false, length = 10, name = "mes_ano")
    private String mesAno; // Formato de período (ex: "07/2026")

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) //Isola a meta por usuário
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false) //Vincula a meta a uma categoria
    private Categoria categoria;
}