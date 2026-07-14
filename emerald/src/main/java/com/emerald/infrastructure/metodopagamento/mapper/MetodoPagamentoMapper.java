package com.emerald.infrastructure.metodopagamento.mapper;

import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoRequestDTO;
import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoResponseDTO;
import com.emerald.infrastructure.metodopagamento.entity.MetodoPagamento;
import org.springframework.stereotype.Component;

@Component // Permite que o Spring gerencie e injete este Mapper no Service
public class MetodoPagamentoMapper {

    // Converte o DTO do formulário do React para a Entidade JPA
    public MetodoPagamento toEntity(MetodoPagamentoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        MetodoPagamento metodo = new MetodoPagamento();
        metodo.setNomeMetodo(dto.getNomeMetodo());

        return metodo;
    }

    // Converte a Entidade do banco de dados em um DTO seguro e limpo para o React
    public MetodoPagamentoResponseDTO toResponseDto(MetodoPagamento metodo) {
        if (metodo == null) {
            return null;
        }

        MetodoPagamentoResponseDTO dto = new MetodoPagamentoResponseDTO();
        dto.setId(metodo.getId());
        dto.setNomeMetodo(metodo.getNomeMetodo());

        return dto;
    }
}