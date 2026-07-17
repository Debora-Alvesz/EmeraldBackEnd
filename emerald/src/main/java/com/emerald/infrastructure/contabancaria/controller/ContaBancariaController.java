package com.emerald.infrastructure.contabancaria.controller;

import com.emerald.infrastructure.contabancaria.dto.ContaBancariaRequestDTO;
import com.emerald.infrastructure.contabancaria.dto.ContaBancariaResponseDTO;
import com.emerald.infrastructure.contabancaria.service.ContaBancariaIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contas-bancarias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ContaBancariaController {

    private final ContaBancariaIService contaBancariaService;

    @PostMapping
    public ResponseEntity<ContaBancariaResponseDTO> save(@Valid @RequestBody ContaBancariaRequestDTO request) {
        // Realiza a persistência de uma nova conta bancária no sistema.
        ContaBancariaResponseDTO novaConta = contaBancariaService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ContaBancariaResponseDTO>> findByUsuarioId(@PathVariable UUID usuarioId) {
        // Retorna a listagem de contas bancárias associadas a um identificador de usuário.
        List<ContaBancariaResponseDTO> contas = contaBancariaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<ContaBancariaResponseDTO> findById(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Busca os detalhes de uma conta bancária específica baseando-se no ID e na posse do registro.
        ContaBancariaResponseDTO conta = contaBancariaService.findById(id, usuarioId);
        return ResponseEntity.ok(conta);
    }

    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<ContaBancariaResponseDTO> update(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId,
            @Valid @RequestBody ContaBancariaRequestDTO request) {
        // Atualiza as informações cadastrais de uma conta bancária existente após checagem de propriedade.
        ContaBancariaResponseDTO contaAtualizada = contaBancariaService.update(id, usuarioId, request);
        return ResponseEntity.ok(contaAtualizada);
    }

    @DeleteMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        // Remove logicamente ou fisicamente uma conta bancária do banco de dados do sistema.
        contaBancariaService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuario/{usuarioId}/extrato")
    public ResponseEntity<List<String>> obterExtratoMensal(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        // Operação em estágio de protótipo: aguarda a finalização das regras da entidade de transações.
        List<String> extrato = contaBancariaService.obterExtratoMensal(id, usuarioId, mes, ano);
        return ResponseEntity.ok(extrato);
    }
}