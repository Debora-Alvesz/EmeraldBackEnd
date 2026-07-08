package com.emerald.infrastructure.metafinanceira.controller;

import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraRequestDTO;
import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraResponseDTO;
import com.emerald.infrastructure.metafinanceira.service.MetaFinanceiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metas-financeiras")
@RequiredArgsConstructor
public class MetaFinanceiraController {

    private final MetaFinanceiraService metaFinanceiraService;

    @PostMapping
    public ResponseEntity<MetaFinanceiraResponseDTO> save(@Valid @RequestBody MetaFinanceiraRequestDTO request) {
        // Realiza a persistência de uma nova meta financeira no sistema.
        MetaFinanceiraResponseDTO novaMeta = metaFinanceiraService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMeta);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MetaFinanceiraResponseDTO>> findByUsuarioId(@PathVariable UUID usuarioId) {
        // Retorna a listagem de metas financeiras associadas a um identificador de usuário.
        List<MetaFinanceiraResponseDTO> metas = metaFinanceiraService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<MetaFinanceiraResponseDTO> findById(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Busca os detalhes de uma meta específica baseando-se no ID e na posse do registro.
        MetaFinanceiraResponseDTO meta = metaFinanceiraService.findById(id, usuarioId);
        return ResponseEntity.ok(meta);
    }

    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<MetaFinanceiraResponseDTO> update(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId,
            @Valid @RequestBody MetaFinanceiraRequestDTO request) {
        // Atualiza as informações de uma meta existente após checagem de propriedade.
        MetaFinanceiraResponseDTO metaAtualizada = metaFinanceiraService.update(id, usuarioId, request);
        return ResponseEntity.ok(metaAtualizada);
    }

    @DeleteMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Remove fisicamente uma meta financeira do banco de dados do sistema.
        metaFinanceiraService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuario/{usuarioId}/progresso")
    public ResponseEntity<Double> calcularProgresso(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Operação em estágio de protótipo: calcula a porcentagem de consumo do teto de gastos.
        Double progresso = metaFinanceiraService.calcularProgressoDaMeta(id, usuarioId);
        return ResponseEntity.ok(progresso);
    }

    @GetMapping("/{id}/usuario/{usuarioId}/alerta")
    public ResponseEntity<String> emitirAlerta(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Operação em estágio de protótipo: verifica e emite avisos de estouro de limite.
        String alerta = metaFinanceiraService.emitirAlertaDeEstouro(id, usuarioId);
        return ResponseEntity.ok(alerta);
    }
}