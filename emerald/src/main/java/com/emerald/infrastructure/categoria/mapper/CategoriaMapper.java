package com.emerald.infrastructure.categoria.mapper;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;
import com.emerald.infrastructure.categoria.entity.Categoria;
import org.springframework.stereotype.Component;

@Component // Permite que o Spring gerencie e injete este Mapper onde for necessário
public class CategoriaMapper {

    // Transforma o DTO vindo do formulário do React na Entidade que o JPA consegue salvar
    public Categoria toEntity(CategoriaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setTipo(dto.getTipo().toUpperCase()); // Salva sempre em maiúsculo para manter o padrão (RECEITA/DESPESA)

        // O relacionamento com o Usuário é injetado diretamente no Service por motivos de segurança
        return categoria;
    }

    // Transforma a Entidade do banco de dados em um DTO seguro para enviar ao React
    public CategoriaResponseDTO toResponseDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setTipo(categoria.getTipo());

        // Mapeia apenas o ID do usuário para proteger os dados sensíveis da entidade Usuario
        if (categoria.getUsuario() != null) {
            dto.setUsuarioId(categoria.getUsuario().getId());
        }

        return dto;
    }
}