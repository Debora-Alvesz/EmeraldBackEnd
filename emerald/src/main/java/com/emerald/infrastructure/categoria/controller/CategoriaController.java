package com.emerald.infrastructure.categoria.controller;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;
import com.emerald.infrastructure.categoria.service.CategoriaIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaIService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> save(
            @Valid @RequestBody CategoriaRequestDTO request) {
        // Inicializa a criação e persistência de uma nova categoria.
        CategoriaResponseDTO novaCategoria = categoriaService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CategoriaResponseDTO>> findByUsuarioId(
            @PathVariable UUID usuarioId) {
        // Lista todas as categorias vinculadas a um determinado ID de usuário.
        List<CategoriaResponseDTO> categorias = categoriaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<CategoriaResponseDTO> findById(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Busca os dados de uma categoria de forma isolada e segura contra IDOR.
        CategoriaResponseDTO categoria = categoriaService.findById(id, usuarioId);
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<CategoriaResponseDTO> update(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId,
            @Valid @RequestBody CategoriaRequestDTO request) {
        // Processa as atualizações de valores de uma categoria existente.
        CategoriaResponseDTO categoriaAtualizada =
                categoriaService.update(id, usuarioId, request);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Realiza a deleção lógica ou física do recurso caso o usuário seja o dono.
        categoriaService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}