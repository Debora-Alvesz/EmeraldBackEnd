package com.emerald.infrastructure.categoria.controller;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;
import com.emerald.infrastructure.categoria.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Rota para criar uma nova categoria personalizada para um usuário específico
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO novaCategoria = categoriaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    // Rota para listar todas as categorias que pertencem estritamente a um usuário específico
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        List<CategoriaResponseDTO> categorias = categoriaService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(categorias);
    }

    // Rota para buscar os detalhes de uma única categoria pelo seu ID exclusivo
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable UUID id) {
        CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    // Rota para atualizar o nome descritivo ou o tipo da categoria (RECEITA/DESPESA)
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO categoriaAtualizada = categoriaService.atualizar(id, request);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    // Rota para excluir uma categoria permanentemente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}