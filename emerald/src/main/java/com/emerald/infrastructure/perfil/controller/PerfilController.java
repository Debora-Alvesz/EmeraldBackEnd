package com.emerald.infrastructure.perfil.controller;

import com.emerald.infrastructure.perfil.dto.PerfilRequestDTO;
import com.emerald.infrastructure.perfil.dto.PerfilResponseDTO;
import com.emerald.infrastructure.perfil.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @PostMapping
    public ResponseEntity<PerfilResponseDTO> save(@RequestBody @Valid PerfilRequestDTO request) {
        // Realiza a persistência de um novo perfil de acesso no sistema global.
        PerfilResponseDTO response = perfilService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PerfilResponseDTO>> findAll() {
        // Retorna a listagem completa de todos os perfis cadastrados na base de dados.
        List<PerfilResponseDTO> perfis = perfilService.findAll();
        return ResponseEntity.ok(perfis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> findById(@PathVariable Long id) {
        // Busca os detalhes e propriedades de um perfil específico através do ID.
        PerfilResponseDTO response = perfilService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PerfilRequestDTO request) {
        // Atualiza a parametrização ou nomenclatura de um perfil global existente.
        PerfilResponseDTO response = perfilService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Remove fisicamente um perfil de acesso do banco de dados do sistema.
        perfilService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissoes")
    public ResponseEntity<List<String>> obterPermissoes(@PathVariable Long id) {
        // Consulta e exporta a listagem de escopos autorizados para o perfil informado.
        List<String> permissoes = perfilService.obterPermissoes(id);
        return ResponseEntity.ok(permissoes);
    }
}