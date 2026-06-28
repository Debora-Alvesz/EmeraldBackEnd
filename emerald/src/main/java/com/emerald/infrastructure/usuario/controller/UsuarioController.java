package com.emerald.infrastructure.usuario.controller;

import com.emerald.infrastructure.usuario.dto.LoginRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioResponseDTO;
import com.emerald.infrastructure.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor // Injeta o UsuarioService automaticamente via construtor do Lombok
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Rota para cadastrar um novo usuário (Sempre receberá o perfil padrão "USER")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO novoUsuario = usuarioService.criar(request);
        // Retorna Status 201 Created com o usuário recém-criado
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // Rota de autenticação (+ autenticar do seu diagrama) para o React fazer login
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO loginRequest) {
        UsuarioResponseDTO usuarioAutenticado = usuarioService.autenticar(loginRequest);
        // Retorna Status 200 OK caso as credenciais estejam corretas
        return ResponseEntity.ok(usuarioAutenticado);
    }

    // Rota para listar todos os usuários cadastrados
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // Rota para buscar um usuário específico pelo ID (usando UUID)
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    // Rota para atualizar os dados de um usuário existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    // Rota para deletar um usuário do sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioService.deletar(id);
        // Retorna Status 204 No Content (Sucesso, sem corpo de resposta)
        return ResponseEntity.noContent().build();
    }
}