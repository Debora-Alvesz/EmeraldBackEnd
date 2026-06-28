package com.emerald.infrastructure.usuario.mapper;

import com.emerald.infrastructure.usuario.dto.UsuarioRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioResponseDTO;
import com.emerald.infrastructure.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

@Component // Permite que o Spring injete este Mapper no seu UsuarioService
public class UsuarioMapper {

    // Converte os dados recebidos do React (DTO) para a Entidade que vai para o banco de dados
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha()); // A senha vai limpa por enquanto (antes do BCrypt)
        return usuario;
    }

    // Converte a Entidade do banco para o DTO que será devolvido com segurança para o React
    public UsuarioResponseDTO toResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());

        // Evita erro de NullPointerException caso o usuário de alguma forma não tenha perfil atrelado
        if (usuario.getPerfil() != null) {
            // Mapeia apenas a String do nome do perfil (ex: "USER") em vez de expor a entidade Perfil inteira
            dto.setNomePerfil(usuario.getPerfil().getNomePerfil());
        }

        return dto;
    }
}