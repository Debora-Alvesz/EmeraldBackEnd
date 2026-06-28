package com.emerald.infrastructure.usuario.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UsuarioResponseDTO {
    private UUID id;
    private String nome;
    private String email;
    private String nomePerfil; // Retorna apenas o nome do perfil (ex: "USER") para o React saber o nível de acesso
}