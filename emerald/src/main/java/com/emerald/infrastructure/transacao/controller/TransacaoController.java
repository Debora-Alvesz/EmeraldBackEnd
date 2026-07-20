package com.emerald.infrastructure.transacao.controller;

import com.emerald.infrastructure.transacao.dto.TransacaoRequestDTO;
import com.emerald.infrastructure.transacao.dto.TransacaoResponseDTO;
import com.emerald.infrastructure.transacao.service.TransacaoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransacaoController {

    private final TransacaoIService transacaoService;

    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> save(@Valid @RequestBody TransacaoRequestDTO request) {
        // Realiza o registro de uma nova transação financeira no sistema.
        TransacaoResponseDTO response = transacaoService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> findById(@PathVariable UUID id) {
        // Busca os detalhes de uma transação específica por meio do ID identificador.
        TransacaoResponseDTO response = transacaoService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponseDTO>> findByUsuario(@PathVariable UUID usuarioId) {
        // Retorna o histórico de todas as transações realizadas por um determinado usuário.
        List<TransacaoResponseDTO> transacoes = transacaoService.findByUsuario(usuarioId);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/usuario/{usuarioId}/filtro")
    public ResponseEntity<List<TransacaoResponseDTO>> filtrarPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dataFim) {
        // Filtra e retorna as transações de um usuário dentro de um intervalo de datas fornecido.
        List<TransacaoResponseDTO> transacoes = transacaoService.filtrarPorPeriodo(usuarioId, dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Exclui o registro físico de uma transação e estorna seu valor no saldo bancário.
        transacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransacaoRequestDTO request) {
        // Atualiza os dados de uma transação existente e recalcula o saldo da conta correspondente.
        TransacaoResponseDTO response = transacaoService.update(id, request);
        return ResponseEntity.ok(response);
    }
}