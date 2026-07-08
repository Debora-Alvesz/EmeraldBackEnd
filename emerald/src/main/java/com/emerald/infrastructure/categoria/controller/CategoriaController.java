package com.emerald.infrastructure.categoria.controller;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;
import com.emerald.infrastructure.categoria.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Cria uma nova categoria
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> save(
            @Valid @RequestBody CategoriaRequestDTO request) {

        CategoriaResponseDTO novaCategoria = categoriaService.save(request);

        // Retorna status 201 (CREATED) com o objeto criado
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    // Retorna todas as categorias de um usuário específico
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CategoriaResponseDTO>> findByUsuarioId(
            @PathVariable UUID usuarioId) {

        List<CategoriaResponseDTO> categorias = categoriaService.findByUsuarioId(usuarioId);

        return ResponseEntity.ok(categorias);
    }

    // Busca uma categoria específica pelo ID
    // O id e o usuarioId vêm pela URL para validação de segurança
    @GetMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<CategoriaResponseDTO> findById(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {

        CategoriaResponseDTO categoria = categoriaService.findById(id, usuarioId);

        return ResponseEntity.ok(categoria);
    }

    // Atualiza uma categoria existente
    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<CategoriaResponseDTO> update(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId,
            @Valid @RequestBody CategoriaRequestDTO request) {

        CategoriaResponseDTO categoriaAtualizada =
                categoriaService.update(id, usuarioId, request);

        return ResponseEntity.ok(categoriaAtualizada);
    }

    // Remove uma categoria
    @DeleteMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {

        categoriaService.delete(id, usuarioId);

        // Retorna status 204 (NO CONTENT), indicando sucesso sem corpo na resposta
        return ResponseEntity.noContent().build();
    }
}