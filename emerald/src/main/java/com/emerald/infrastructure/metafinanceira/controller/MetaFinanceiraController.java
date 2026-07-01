package com.emerald.infrastructure.metafinanceira.controller;

import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraRequestDTO;
import com.emerald.infrastructure.metafinanceira.dto.MetaFinanceiraResponseDTO;
import com.emerald.infrastructure.metafinanceira.service.MetaFinanceiraService;
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
@RequestMapping("/api/v1/metas-financeiras")
@RequiredArgsConstructor
public class MetaFinanceiraController {

    private final MetaFinanceiraService metaFinanceiraService;

    // Rota para cadastrar uma nova meta
    @PostMapping
    public ResponseEntity<MetaFinanceiraResponseDTO> criar(@Valid @RequestBody MetaFinanceiraRequestDTO request) {
        MetaFinanceiraResponseDTO novaMeta = metaFinanceiraService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMeta);
    }

    // Rota para listar todas as metas de um usuário
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MetaFinanceiraResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        List<MetaFinanceiraResponseDTO> metas = metaFinanceiraService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    // Rota para buscar os detalhes de uma meta pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<MetaFinanceiraResponseDTO> buscarPorId(@PathVariable UUID id) {
        MetaFinanceiraResponseDTO meta = metaFinanceiraService.buscarPorId(id);
        return ResponseEntity.ok(meta);
    }

    // Rota para atualizar os dados de uma meta existente
    @PutMapping("/{id}")
    public ResponseEntity<MetaFinanceiraResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MetaFinanceiraRequestDTO request) {
        MetaFinanceiraResponseDTO metaAtualizada = metaFinanceiraService.atualizar(id, request);
        return ResponseEntity.ok(metaAtualizada);
    }

    // Rota para remover uma meta do sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        metaFinanceiraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Rota do diagrama para consultar a porcentagem de progresso da meta
    @GetMapping("/{id}/progresso")
    public ResponseEntity<Double> calcularProgresso(@PathVariable UUID id) {
        Double progresso = metaFinanceiraService.calcularProgressoDaMeta(id);
        return ResponseEntity.ok(progresso);
    }

    // Rota do diagrama para verificar e emitir alertas sobre a meta
    @GetMapping("/{id}/alerta")
    public ResponseEntity<String> emitirAlerta(@PathVariable UUID id) {
        String alerta = metaFinanceiraService.emitirAlertaDeEstouro(id);
        return ResponseEntity.ok(alerta);
    }
}