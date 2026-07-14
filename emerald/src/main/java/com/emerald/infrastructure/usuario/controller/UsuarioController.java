package com.emerald.infrastructure.usuario.controller;

import com.emerald.infrastructure.usuario.dto.LoginRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioResponseDTO;
import com.emerald.infrastructure.usuario.service.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioIService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> save(@Valid @RequestBody UsuarioRequestDTO request) {
        // Realiza a persistência de um novo usuário com perfil básico no sistema.
        UsuarioResponseDTO novoUsuario = usuarioService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO loginRequest) {
        // Processa o fluxo de autenticação e validação cadastral de credenciais de acesso.
        UsuarioResponseDTO usuarioAutenticado = usuarioService.autenticar(loginRequest);
        return ResponseEntity.ok(usuarioAutenticado);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        // Retorna a listagem completa de todos os usuários registrados na base de dados.
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable UUID id) {
        // Busca as propriedades e dados detalhados de um usuário específico por meio do ID.
        UsuarioResponseDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        // Atualiza as informações cadastrais e dados de login de um usuário existente.
        UsuarioResponseDTO usuarioAtualizado = usuarioService.update(id, request);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Remove fisicamente o registro de um usuário da base de dados do sistema.
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}