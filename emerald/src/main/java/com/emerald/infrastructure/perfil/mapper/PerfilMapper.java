package com.emerald.infrastructure.perfil.mapper;

import com.emerald.infrastructure.perfil.dto.PerfilRequestDTO;
import com.emerald.infrastructure.perfil.dto.PerfilResponseDTO;
import com.emerald.infrastructure.perfil.entity.Perfil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component // Permite que o Spring injete o Mapper no Service usando o @RequiredArgsConstructor
public class PerfilMapper {

    // Converte o DTO que vem do React para a Entidade do banco de dados
    public Perfil toEntity(PerfilRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Perfil perfil = new Perfil();
        perfil.setNomePerfil(dto.getNomePerfil());
        return perfil;
    }

    // Converte a Entidade do banco para o DTO que o React vai ler
    public PerfilResponseDTO toResponseDto(Perfil perfil) {
        if (perfil == null) {
            return null;
        }
        PerfilResponseDTO dto = new PerfilResponseDTO();
        dto.setId(perfil.getId());
        dto.setNomePerfil(perfil.getNomePerfil());
        return dto;
    }

    // Converte uma lista de Entidades para uma lista de DTOs (útil para o buscarTodos)
    public List<PerfilResponseDTO> toResponseDtoList(List<Perfil> perfis) {
        if (perfis == null) {
            return null;
        }
        return perfis.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}