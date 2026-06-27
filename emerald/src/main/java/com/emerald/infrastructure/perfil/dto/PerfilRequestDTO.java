package com.emerald.infrastructure.perfil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilRequestDTO {

    @NotBlank(message = "O nome do perfil é obrigatório.")
    @Size(max = 50, message = "O nome do perfil não pode passar de 50 caracteres.")
    private String nomePerfil;
}