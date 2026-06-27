package com.emerald.infrastructure.perfil.controller;

import com.emerald.infrastructure.perfil.dto.PerfilRequestDTO;
import com.emerald.infrastructure.perfil.dto.PerfilResponseDTO;
import com.emerald.infrastructure.perfil.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfis")
@CrossOrigin(origins = "*") // Evita o bloqueio de requisições (CORS) quando o front-end tentar se conectar
@RequiredArgsConstructor // Injeta automaticamente o PerfilService via construtor em tempo de compilação
public class PerfilController {

    private final PerfilService perfilService;

    @PostMapping
    // @Valid: Intercepta a requisição e valida o DTO antes de executar o método. Se falhar, barra aqui.
    public ResponseEntity<PerfilResponseDTO> criar(@RequestBody @Valid PerfilRequestDTO request) {
        PerfilResponseDTO response = perfilService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PerfilResponseDTO>> buscarTodos() {
        List<PerfilResponseDTO> perfis = perfilService.buscarTodos();
        return ResponseEntity.ok(perfis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> buscarPorId(@PathVariable Long id) {
        PerfilResponseDTO response = perfilService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PerfilRequestDTO request) {
        PerfilResponseDTO response = perfilService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    // Retorna HTTP 204 (No Content), que é o padrão sem corpo de resposta para deleções com sucesso
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        perfilService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissoes")
    public ResponseEntity<List<String>> obterPermissoes(@PathVariable Long id) {
        List<String> permissoes = perfilService.obterPermissoes(id);
        return ResponseEntity.ok(permissoes);
    }
}