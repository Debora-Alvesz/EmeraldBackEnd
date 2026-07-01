package com.emerald.infrastructure.metafinanceira.mapper;

import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraRequestDTO;
import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraResponseDTO;
import com.emerald.infrastructure.metafinanceira.entity.MetaFinanceira;
import org.springframework.stereotype.Component;

@Component // Permite que o Spring gerencie e injete este Mapper no Service
public class MetaFinanceiraMapper {

    // Converte o DTO do formulário do React para a Entidade JPA
    public MetaFinanceira toEntity(MetaFinanceiraRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        MetaFinanceira meta = new MetaFinanceira();
        meta.setValorLimite(dto.getValorLimite());
        meta.setMesAno(dto.getMesAno());

        // Os relacionamentos com Usuário e Categoria serão injetados no Service por segurança
        return meta;
    }

    // Converte a Entidade do banco de dados em um DTO seguro e limpo para o React
    public MetaFinanceiraResponseDTO toResponseDto(MetaFinanceira meta) {
        if (meta == null) {
            return null;
        }

        MetaFinanceiraResponseDTO dto = new MetaFinanceiraResponseDTO();
        dto.setId(meta.getId());
        dto.setValorLimite(meta.getValorLimite());
        dto.setMesAno(meta.getMesAno());

        // Extrai com segurança apenas os IDs para o DTO de resposta
        if (meta.getUsuario() != null) {
            dto.setUsuarioId(meta.getUsuario().getId());
        }

        if (meta.getCategoria() != null) {
            dto.setCategoriaId(meta.getCategoria().getId());
            dto.setNomeCategoria(meta.getCategoria().getNome()); // Facilita exibição no Front-end
        }

        return dto;
    }
}